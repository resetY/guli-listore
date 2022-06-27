package com.yuan.gulicart.vo;

import lombok.Data;

/**
 * 临时用户
 * To为传输对象，这里传输给用户的
 * **/
@Data
public class UserInfoTo {
        private Long userId;
        private String userKey;
        private boolean tempUser = false; //标识：用于判断有无cookie用户
}
