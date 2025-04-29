package com.voyah.ai.basecar.phone;

import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.PinyinUtils;
import com.voyah.ds.common.entity.domains.call.ContactInfo;
import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author:lcy
 * @data:2024/3/4
 **/
public class SearchContactUtils {

    private static final String TAG = SearchContactUtils.class.getSimpleName();
    private static List<ContactInfo> searchList = new ArrayList<>();
    private static List<ContactNumberInfo> numberList = new ArrayList<>();

    public static List<ContactInfo> searchListByName(String name, List<ContactInfo> list) {
        String searchName = name.toLowerCase();
        List<ContactInfo> mList = new ArrayList<>();
        if (StringUtils.isBlank(searchName) || list.isEmpty()) {
            return mList;
        }
        for (ContactInfo contactsInfo : list) {
            String contactName = contactsInfo.getName().toLowerCase();
            if (contactsInfo.getNamePinYin().contains(PinyinUtils.pinyin(searchName)) || contactName.contains(searchName)) {
                if (!mList.contains(contactsInfo))
                    mList.add(contactsInfo);
            }
        }

        if (mList.isEmpty()) {
            for (ContactInfo contactsInfo : list) {
                String contactName = contactsInfo.getName().toLowerCase();
                if (contactName.length() > 1 && searchName.length() > contactName.length() && searchName.contains(contactName)) {
                    if (!mList.contains(contactsInfo))
                        mList.add(contactsInfo);
                }
            }
        }
        return mList;
    }

    public static List<ContactInfo> searchListByNumber(String number_front, String number_end, String number, List<ContactInfo> list) {
        List<ContactInfo> mList = new ArrayList<>();
        if (list.isEmpty()) {
            return mList;
        }
        //没有号段
        if (StringUtils.isBlank(number_front) && StringUtils.isBlank(number_end) && !StringUtils.isBlank(number)) {
            for (ContactInfo contactsInfo : list) {
                numberList.clear();
                List<ContactNumberInfo> contactNumberInfoList = contactsInfo.getNumberInfoList();
                if (contactNumberInfoList.isEmpty())
                    continue;
                for (ContactNumberInfo numberInfo : contactNumberInfoList) {
                    String phone_number = numberInfo.getNumber();
                    if (StringUtils.equals(phone_number, number)) {
                        numberList.add(numberInfo);
                    }
                }
                if (!numberList.isEmpty()) {
                    contactsInfo.setNumberInfoList(numberList);
                    mList.add(contactsInfo);
                }
            }
        } else {
            //有号段
            for (ContactInfo contactsInfo : list) {
                numberList.clear();
                List<ContactNumberInfo> contactNumberInfoList = contactsInfo.getNumberInfoList();
                if (contactNumberInfoList.isEmpty())
                    continue;
                for (ContactNumberInfo contactNumberInfo : contactNumberInfoList) {
                    String contactNumber = contactNumberInfo.getNumber();
                    String numberSegment;
                    if (StringUtils.isBlank(contactNumber))
                        continue;
                    if (!StringUtils.isBlank(number_front) && StringUtils.isBlank(number_end)) {
                        //开头号段
                        if (contactNumber.length() < number_front.length())
                            continue;
                        int number_len = number_front.length();
                        numberSegment = contactNumber.substring(0, number_len);
                        if (StringUtils.isBlank(numberSegment))
                            continue;
                        if (StringUtils.equals(numberSegment, number_front)) {
                            numberList.add(contactNumberInfo);
                        }
                    } else if (StringUtils.isBlank(number_front) && !StringUtils.isBlank(number_end)) {
                        //结尾号段
                        if (contactNumber.length() < number_end.length())
                            continue;
                        int number_len = number_end.length();
                        numberSegment = contactNumber.substring(contactNumber.length() - number_len);
                        if (StringUtils.isBlank(numberSegment))
                            continue;
                        if (StringUtils.equals(numberSegment, number_end)) {
                            numberList.add(contactNumberInfo);
                        }
                    } else if (!StringUtils.isBlank(number_front) && !StringUtils.isBlank(number_end)) {
                        //组合号段
                        if (contactNumber.length() < number_front.length() || contactNumber.length() < number_end.length())
                            continue;
                        int number_front_len = number_front.length();
                        int number_end_len = number_end.length();
                        String number_front_segment = contactNumber.substring(0, number_front_len);
                        String number_end_segment = contactNumber.substring(contactNumber.length() - number_end_len);
                        if (StringUtils.isBlank(number_front_segment) || StringUtils.isBlank(number_end_segment))
                            continue;
                        if (StringUtils.equals(number_front_segment, number_front) && StringUtils.equals(number_end_segment, number_end)) {
                            numberList.add(contactNumberInfo);
                        }
                    }
                }
                if (!numberList.isEmpty()) {
                    ContactInfo contactInfo1 = new ContactInfo();
                    contactInfo1.setName(contactsInfo.getName());
                    contactInfo1.setNamePinYin(contactsInfo.getNamePinYin());
                    contactInfo1.setNumberInfoList(numberList);
                    mList.add(contactInfo1);
                }
            }
        }
        return mList;
    }

    public static List<ContactInfo> searchListByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactInfo> list) {
        List<ContactInfo> mList;
        numberList.clear();
        searchList.clear();
        searchList = searchListByName(name, list);
        mList = searchListByNumber(number_front, number_end, number, searchList);
        return mList;
    }

    public static List<ContactNumberInfo> selectByName(String name, List<ContactNumberInfo> list) {
        String selectName = name.toLowerCase();
        List<ContactNumberInfo> mList = new ArrayList<>();
        if (StringUtils.isBlank(selectName) || list.isEmpty()) {
            return mList;
        }
        for (ContactNumberInfo contactNumberInfo : list) {
            String contactName = contactNumberInfo.getName().toLowerCase();
            if (StringUtils.equals(contactNumberInfo.getNamePinYin(), PinyinUtils.pinyin(selectName)) || StringUtils.equals(contactName, selectName)) {
                mList.add(contactNumberInfo);
            }
        }
        return mList;
    }

    public static List<ContactNumberInfo> selectByNumber(String number_front, String number_end, String number, List<ContactNumberInfo> contactNumberInfoList) {
        numberList.clear();
        if (contactNumberInfoList.isEmpty())
            return numberList;
        //没有号段
        if (StringUtils.isBlank(number_front) && StringUtils.isBlank(number_end) && !StringUtils.isBlank(number)) {
            for (ContactNumberInfo contactNumberInfo : contactNumberInfoList) {
                String contactNumber = contactNumberInfo.getNumber();
                if (StringUtils.equals(contactNumber, number)) {
                    numberList.add(contactNumberInfo);
                }
            }
        } else {
            //有号段
            for (ContactNumberInfo contactNumberInfo : contactNumberInfoList) {
                String contactNumber = contactNumberInfo.getNumber();
                String numberSegment;
                if (StringUtils.isBlank(contactNumber))
                    continue;
                if (!StringUtils.isBlank(number_front) && StringUtils.isBlank(number_end)) {
                    //开头号段
                    if (contactNumber.length() < number_front.length())
                        continue;
                    int number_len = number_front.length();
                    numberSegment = contactNumber.substring(0, number_len);
                    if (StringUtils.isBlank(numberSegment))
                        continue;
                    if (StringUtils.equals(numberSegment, number_front)) {
                        numberList.add(contactNumberInfo);
                    }

                } else if (StringUtils.isBlank(number_front) && !StringUtils.isBlank(number_end)) {
                    //结尾号段
                    if (contactNumber.length() < number_end.length())
                        continue;
                    int number_len = number_end.length();
                    numberSegment = contactNumber.substring(contactNumber.length() - number_len);
                    if (StringUtils.isBlank(numberSegment))
                        continue;
                    if (StringUtils.equals(numberSegment, number_end)) {
                        numberList.add(contactNumberInfo);
                    }
                } else if (!StringUtils.isBlank(number_front) && !StringUtils.isBlank(number_end)) {
                    //组合号段
                    if (contactNumber.length() < number_front.length() || contactNumber.length() < number_end.length())
                        continue;
                    int number_front_len = number_front.length();
                    int number_end_len = number_end.length();
                    String number_front_segment = contactNumber.substring(0, number_front_len);
                    String number_end_segment = contactNumber.substring(contactNumber.length() - number_end_len);
                    if (StringUtils.isBlank(number_front_segment) || StringUtils.isBlank(number_end_segment))
                        continue;
                    if (StringUtils.equals(number_front_segment, number_front) && StringUtils.equals(number_end_segment, number_end)) {
                        numberList.add(contactNumberInfo);
                    }
                }
            }

        }
        return numberList;
    }

    public static List<ContactNumberInfo> selectByNameAndNumber(String name, String number_front, String number_end, String number, List<ContactNumberInfo> list) {
        return selectByNumber(number_front, number_end, number, selectByName(name, list));
    }

    public static String getNumberByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList) {
        String number = "";
        index = Math.abs(index);
        if (isMinus) {
            Collections.reverse(numberInfoList);
            number = numberInfoList.get(index - 1).getNumber();
            Collections.reverse(numberInfoList);
        } else {
            number = numberInfoList.get(index - 1).getNumber();
        }
        return number;
    }

    public static String getNameByIndex(boolean isMinus, int index, List<ContactNumberInfo> numberInfoList) {
        String name = "";
        index = Math.abs(index);
        if (isMinus) {
            Collections.reverse(numberInfoList);
            name = numberInfoList.get(index - 1).getName();
            Collections.reverse(numberInfoList);
        } else {
            name = numberInfoList.get(index - 1).getName();
        }
        return name;
    }

    public static List<ContactNumberInfo> getNumberListByIndex(boolean isMinus, int index, List<ContactInfo> contactsInfoList) {
        List<ContactNumberInfo> contactNumberInfoList;
        index = Math.abs(index);
        if (isMinus) {
            Collections.reverse(contactsInfoList);
            contactNumberInfoList = contactsInfoList.get(index - 1).getNumberInfoList();
            Collections.reverse(contactsInfoList);
        } else {
            contactNumberInfoList = contactsInfoList.get(index - 1).getNumberInfoList();
        }
        return contactNumberInfoList;
    }

    public static List<ContactNumberInfo> getContactToNumberList(List<ContactInfo> contactInfoList) {
        numberList.clear();
        for (ContactInfo contact : contactInfoList) {
            numberList.addAll(contact.getNumberInfoList());
        }
        return numberList;
    }

    //判断黄页中是否有指定数据
    public static boolean isYellowPageContainsName(List<ContactInfo> yellowPageList, String name) {
        boolean isContains = false;
        String containName = name.toLowerCase();
        if (!yellowPageList.isEmpty() && !StringUtils.isBlank(containName)) {
            for (ContactInfo contactsInfo : yellowPageList) {
                String contactName = contactsInfo.getName().toLowerCase();
                if (contactsInfo.getNamePinYin().contains(PinyinUtils.pinyin(containName)) || contactName.contains(containName)) {
                    return true;
                }
            }

            for (ContactInfo contactsInfo : yellowPageList) {
                String contactName = contactsInfo.getName().toLowerCase();
                if (containName.contains(contactName)) {
                    return true;
                }
            }
        }
        LogUtils.d(TAG, "isYellowPageContains isContains:" + isContains);
        return isContains;
    }
}
