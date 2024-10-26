package com.kamal.scm_app.utils;

import com.kamal.scm_app.models.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardData {
    private Integer totalUserCount;
    private Integer totalContactsCount;
    private Integer verifiedUserCount;
    private Integer totalAdminCount;
    private List<String> pendingEmailsForAdminAccess;

}
