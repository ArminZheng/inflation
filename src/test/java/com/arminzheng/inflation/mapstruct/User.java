package com.arminzheng.inflation.mapstruct;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class User {
    private String name;
    private int age;
    private String title;
    private Date startDt;
    private LocalDateTime endDt;
}


