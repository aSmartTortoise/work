package com.example.filter_process;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.ClassAgent;
import com.example.filter_annotation.FlowChartDevices;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

//启用服务
@AutoService(Process.class)
@SupportedAnnotationTypes({"com.example.filter_annotation.FilterParams"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FilterProcessor extends AbstractProcessor {
    private Messager messager;// 用来输出日志、错误或警告信息
    private Elements elementUtils; // 操作元素的工具类
    private final String AGENT_PATH = "assets/agents.txt";
    //生成类的方法
    private HashMap<String, String> intentHashMap = new HashMap<>();
    private HashMap<String, String> packageHashMap = new HashMap<>();
    private List<String> agentNameList = new ArrayList<>();
    private List<String> packageNameList = new ArrayList<>();
    private String packageName;
    private Filer mFiler;  // 用来创建文件
    //虚拟车用到的对象
    private HashMap<String, HashMap<String, String>> carDevicesMap = new HashMap();
//    private HashMap<String, String> carIntentHashMap = new HashMap<>();
//    private HashMap<String, String> carPackageHashMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
        this.mFiler = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>进入解释器初始化");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();  // 版本
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(ClassAgent.class.getCanonicalName());
        set.add(FlowChartDevices.class.getCanonicalName());
        set.add(CarDevices.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "并没有发现 被@FilterParams注解的地方呀");
            return false;
        }
        //1.获取到所有的注解
//        Set<Class<? extends Annotation>> set1 = new HashSet<>();
//        set1.add(ClassAgent.class);
//        set1.add(FlowChartDevices.class);
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWithAny(set1);
        Set<? extends Element> agentElements = roundEnvironment.getElementsAnnotatedWith(ClassAgent.class);
        Set<? extends Element> flowChartElements = roundEnvironment.getElementsAnnotatedWith(FlowChartDevices.class);
        Set<? extends Element> carDevicesElements = roundEnvironment.getElementsAnnotatedWith(CarDevices.class);
        List<Set<? extends Element>> elements = new ArrayList<>();
        elements.add(agentElements);
        elements.add(flowChartElements);
        elements.add(carDevicesElements);
        messager.printMessage(Diagnostic.Kind.NOTE, "elements的个数是：" + elements.size() + "set里的个数是：" + set.size() + " set的类型：");

        messager.printMessage(Diagnostic.Kind.NOTE, "==========ClassAgent.class==========");
        for(Element element : agentElements){
            //因为注解作用在类上所以直接强转。
            TypeElement classElement = (TypeElement) element;
            //获取包名
            packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            //类名  String classNme = CallAgent  String intent = call
            String className = element.getSimpleName().toString();

            agentNameList.add(className);
            packageNameList.add(packageName);
            messager.printMessage(Diagnostic.Kind.NOTE, "当前循环到的类名：" + className);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "==========FlowChartDevices.class==========");
        for(Element element : flowChartElements){
            //因为注解作用在类上所以直接强转。
            TypeElement classElement = (TypeElement) element;
            //获取包名
            packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            //类名  String classNme = CallAgent  String intent = call
            String className = element.getSimpleName().toString();

            String intent;
            boolean isContain = className.contains("ControlControl");
            if (isContain) {
                int firstPosition = className.indexOf("Control", 1);
                int secondPosition = className.indexOf("Control", firstPosition + "Control".length());
                intent = lowerFirstChar(className.substring(0, secondPosition));

            } else {
                String[] arrays = className.split("Control");

                char[] chars = arrays[0].toCharArray();
                intent = (chars[0] + "").toLowerCase();
                for (int i = 1; i < chars.length; i++) {
                    intent += chars[i];
                }
            }


            messager.printMessage(Diagnostic.Kind.NOTE, "当前的intent是：" + intent);
            intentHashMap.put(intent, className);
            packageHashMap.put(intent, packageName);

            messager.printMessage(Diagnostic.Kind.NOTE, "当前循环到的类名：" + className);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "==========CarDevices.class==========");
        for(Element element : carDevicesElements){
            //因为注解作用在类上所以直接强转。
            TypeElement classElement = (TypeElement) element;
            //获取包名
            packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            //类名  String classNme = CallAgent  String intent = call
            String className = element.getSimpleName().toString();

            CarDevices myAnnotation = element.getAnnotation(CarDevices.class);
            if (myAnnotation != null) {
                String value = myAnnotation.carType().toString();
                messager.printMessage(Diagnostic.Kind.NOTE, "注解上的数据是：" + value);
                String[] array = value.split("_");
                StringBuilder ClassName = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    String cur = array[i];
                    ClassName.append(cur.charAt(0));
                    for (int j = 1; j < cur.length(); j++) {
                        ClassName.append((cur.charAt(j) + "").toLowerCase());
                    }
                }
                messager.printMessage(Diagnostic.Kind.NOTE, "类名是：" + ClassName.toString());

                if (!carDevicesMap.containsKey(ClassName.toString())) {
                    carDevicesMap.put(ClassName.toString(), new HashMap<>());
                }
                carDevicesMap.get(ClassName.toString()).put(className, packageName);
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "当前循环到的类名：" + className);
        }


//        for (Set<? extends Element> set1 : elements) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "set1：的数量是："+set1.size());
//            //循环所有注解
//            for (Element element : set1) {
////            //因为注解作用在类上所以直接强转。
//                TypeElement classElement = (TypeElement) element;
//                //获取包名
//                packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
//                //类名  String classNme = CallAgent  String intent = call
//                String className = element.getSimpleName().toString();
//
//                String aa = set1.iterator().next().toString();
//                messager.printMessage(Diagnostic.Kind.NOTE, "当前遍历的注解是：" + aa + "  element：" + element.toString() + " " +
//                        "上面的注解是：");
//
//                if (aa.contains("FlowChartDevices")) {
////               通过 className获取intent,
//                    String intent;
//                    boolean isContain = className.contains("ControlControl");
//                    if (isContain) {
//                        int firstPosition = className.indexOf("Control", 1);
//                        int secondPosition = className.indexOf("Control", firstPosition + "Control".length());
//                        intent = lowerFirstChar(className.substring(0, secondPosition));
//
//                    } else {
//                        String[] arrays = className.split("Control");
//
//                        char[] chars = arrays[0].toCharArray();
//                        intent = (chars[0] + "").toLowerCase();
//                        for (int i = 1; i < chars.length; i++) {
//                            intent += chars[i];
//                        }
//                    }
//
//
//                    messager.printMessage(Diagnostic.Kind.NOTE, "当前的intent是：" + intent);
//                    intentHashMap.put(intent, className);
//                    packageHashMap.put(intent, packageName);
//                } else if (aa.contains("ClassAgent")) {
//
//                } else if (aa.contains("CarDevices")) {
//
//
//
//                }
//                messager.printMessage(Diagnostic.Kind.NOTE, "当前循环到的类名：" + className);
//            }
//        }
        Iterator<? extends TypeElement> iterator = set.iterator();
        messager.printMessage(Diagnostic.Kind.NOTE, "set的大小"+set.size());
        while(iterator.hasNext()) {
            String str = iterator.next().toString();
            if (str.contains("FlowChartDevices")) {
                messager.printMessage(Diagnostic.Kind.NOTE, "生成类：FlowChartDevices");
                createIntent();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成类完成：FlowChartDevices");
            } else if (str.contains("ClassAgent")) {
                messager.printMessage(Diagnostic.Kind.NOTE, "生成类：ClassAgent");
                createAgent();
            } else if (str.contains("CarDevices")) {
                messager.printMessage(Diagnostic.Kind.NOTE, "生成类：CarDevices");
                //20241218 暂时屏蔽carDevice注解的生成
                //createCar();
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "====end===");


        return false;
    }

    private void createCar() {
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator = carDevicesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, String>> entry = iterator.next();
            String curClassName = entry.getKey()+"OperatorDispatcher";
            HashMap<String, String> curMap = entry.getValue();
            Iterator<Map.Entry<String, String>> iterator1 = curMap.entrySet().iterator();


            //先写方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init")
                    .addModifiers(Modifier.PROTECTED) // 添加标识符 public static
                    .addAnnotation(Override.class)
                    .returns(void.class); // 返回类型
            //对应着车里的包名

            while (iterator1.hasNext()) {
                Map.Entry<String, String> map = iterator1.next();
                String className = map.getKey();
                String packageName = map.getValue();
                String key = "";
                //虚拟设备的处理
                String[] arrays = className.split("PropertyOperator");
                char[] chars = arrays[0].toCharArray();
                key = (chars[0] + "").toLowerCase();
                for (int i = 1; i < chars.length; i++) {
                    key += chars[i];
                }
                ClassName hoverboard = ClassName.get(packageName, className);
                methodBuilder = methodBuilder.addStatement("propertyOperatorMap.put(\"" + key.toLowerCase() + "\",new $T())", hoverboard);
            }

            MethodSpec main = methodBuilder
                    .build();

            //父类
            //"com.example.test.domain"
            ClassName baseClass = ClassName.get("com.voyah.ai.device.voyah.common", "BaseOperatorDispatcher");
            //再构建类
            TypeSpec mFilerFactory = TypeSpec.classBuilder(curClassName)
                    .superclass(baseClass)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(main)
                    .build();

            // 生成 java 文件
            //"com.example.test.domain"
            String key = "";
            //虚拟设备的处理
            String[] arrays = curClassName.split("PropertyOperator");
            char[] chars = arrays[0].toCharArray();
            key = (chars[0] + "").toLowerCase();
            for (int i = 1; i < chars.length; i++) {
                key += chars[i];
            }
            JavaFile javaFile = JavaFile.builder("com.voyah.ai.device.voyah.common." + key, mFilerFactory).build();
            try {
//            messager.printMessage(Diagnostic.Kind.NOTE, javaFile.toString());
                javaFile.writeTo(mFiler);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }


    }

    public String lowerFirstChar(String str) {
        char firstChar = str.charAt(0);
        char lowerChar = Character.toLowerCase(firstChar);
        return lowerChar + str.substring(1);
    }

    private void createAgent() {
        String packagePath = "com.voyah.ai.voice.agent";
        //先写方法
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("initAgent")
                .addModifiers(Modifier.PUBLIC) // 添加标识符 public static
                .addAnnotation(Override.class)
                .returns(void.class); // 返回类型
        for (int i = 0; i < agentNameList.size(); i++) {
            ClassName hoverboard = ClassName.get(packageNameList.get(i), agentNameList.get(i));
            methodBuilder = methodBuilder.addStatement("list.add(new $T())", hoverboard);

        }

        MethodSpec main = methodBuilder
                .build();

        //父类
        //"com.example.test.domain"
        ClassName baseClass = ClassName.get(packagePath, "AgentIntent");
        //再构建类
        TypeSpec mFilerFactory = TypeSpec.classBuilder("AgentIntentManager")
                .superclass(baseClass)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();

        // 生成 java 文件
        //"com.example.test.domain"
        JavaFile javaFile = JavaFile.builder(packagePath, mFilerFactory).build();
        try {
//            messager.printMessage(Diagnostic.Kind.NOTE, javaFile.toString());
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void createIntent() {
        //先写方法
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PROTECTED) // 添加标识符 public static
                .addAnnotation(Override.class)
                .returns(void.class); // 返回类型
        Set<String> set = intentHashMap.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String className = intentHashMap.get(key);
            String curPackageName = packageHashMap.get(key);
//            String intentValue = key + "Intent";//airConditionerSwitch + Intent
            ClassName hoverboard = ClassName.get(curPackageName, className);
            methodBuilder = methodBuilder.addStatement("map.put(\"" + key + "\",new $T(context))", hoverboard);

        }
        //创建构造函数
        TypeName contextTypeName = ClassName.get("android.content", "Context");
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextTypeName, "context")
                .addStatement("this.context = context")
                .addStatement("init()")
                .build();

        MethodSpec main = methodBuilder
                .build();

        //成员变量
        FieldSpec contextVariable = FieldSpec.builder(contextTypeName, "context", Modifier.PRIVATE)
                .build();
        //父类
        //"com.example.test.domain"
        ClassName baseClass = ClassName.get("com.voyah.ai.device.voyah.h37.dc.abstractmethod", "BaseIntent");
        //再构建类
        TypeSpec mFilerFactory = TypeSpec.classBuilder("DevicesIntentManager")
                .superclass(baseClass)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor)
                .addMethod(main)
                .addField(contextVariable)
                .build();

        // 生成 java 文件
        //"com.example.test.domain"
        JavaFile javaFile = JavaFile.builder("com.voyah.ai.device.voyah.h37", mFilerFactory).build();
        try {
//            messager.printMessage(Diagnostic.Kind.NOTE, javaFile.toString());
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}