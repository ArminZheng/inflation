package com.arminzheng.inflation.mapstruct;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class User implements Serializable {
    private String name;
    private int age;
    private String title;
    private Date startDt;
    private LocalDateTime endDt;
}


