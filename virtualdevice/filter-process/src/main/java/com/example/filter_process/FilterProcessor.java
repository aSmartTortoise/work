package com.example.filter_process;

import static jdk.xml.internal.SecuritySupport.getResourceAsStream;

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
    //生成类的方法
    private HashMap<String, String> intentHashMap = new HashMap<>();
    private HashMap<String, String> packageHashMap = new HashMap<>();
    private List<String> agentNameList = new ArrayList<>();
    private List<String> packageNameList = new ArrayList<>();
    private String packageName;
    private Filer mFiler;  // 用来创建文件


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
        set.add(FlowChartDevices.class.getCanonicalName());
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
        Set<? extends Element> flowChartElements = roundEnvironment.getElementsAnnotatedWith(FlowChartDevices.class);
        List<Set<? extends Element>> elements = new ArrayList<>();
        elements.add(flowChartElements);
        messager.printMessage(Diagnostic.Kind.NOTE, "elements的个数是：" + elements.size() + "set里的个数是：" + set.size() + " set的类型：" + set.iterator().next().toString());

        for (Set<? extends Element> set1 : elements) {
            //循环所有注解
            for (Element element : set1) {
//            //因为注解作用在类上所以直接强转。
                TypeElement classElement = (TypeElement) element;
                //获取包名
                packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
                //类名  String classNme = CallAgent  String intent = call
                String className = element.getSimpleName().toString();


                agentNameList.add(className);

                messager.printMessage(Diagnostic.Kind.NOTE, "当前循环到的类名：" + className);
            }
        }

        createDevice();


        return false;
    }

    private void createDevice() {
        String packagePath = "com.voyah.vcos.virtualdevice.param.intent";
        //先写方法
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC) // 添加标识符 public static
                .addAnnotation(Override.class)
                .returns(void.class); // 返回类型
        for (int i = 0; i < agentNameList.size(); i++) {
            ClassName hoverboard = ClassName.get(packagePath, agentNameList.get(i));
            methodBuilder = methodBuilder.addStatement("list.add(new $T())", hoverboard);

        }

        MethodSpec main = methodBuilder
                .build();

        //父类
        //"com.example.test.domain"
        ClassName baseClass = ClassName.get(packagePath, "BaseControl");
        //再构建类
        TypeSpec mFilerFactory = TypeSpec.classBuilder("AllDeviceControlManager")
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


}