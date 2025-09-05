package com.example.wangtu.common;

import lombok.Data;


import java.io.Serializable;
//用于通用的删除
@Data
public class DeleteRequest implements Serializable {
    private Long id;
    private static final long serialVersionUID = 1L;

}
