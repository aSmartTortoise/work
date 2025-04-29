package com.voyah.common.model

/**
 *  author : jie wang
 *  date : 2024/4/19 11:33
 *  description : 聊天角色的枚举类
 */
enum class ChatRole {

    USER,
    ASSISTANT;

    open fun fromName(name: String): ChatRole? {
        for (role in values()) {
            if (role.name == name) {
                return role
            }
        }
        return null
    }
}