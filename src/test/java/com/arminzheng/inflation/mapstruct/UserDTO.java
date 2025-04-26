package com.arminzheng.inflation.mapstruct;

import java.util.Date;
import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private int age;
    private String showTitle;
    private String employeeStartDt;
    private Date endDt;
    private String external;
}
