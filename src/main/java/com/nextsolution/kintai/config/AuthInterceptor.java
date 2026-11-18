package com.nextsolution.kintai.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * /api/** へのアクセスをサーバー側で検証する。
 * - ログイン（セッション）必須
 * - 管理者・管理職専用 API はロールを確認
 * - 一般社員は自分の userId のデータのみ操作可能
 */
public class AuthInterceptor implements HandlerInterceptor {
    public static final String SESSION_KEY = "loginUser";
    private static final Set<String> STAFF = Set.of("MANAGER", "HR", "ADMIN");

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String path = req.getRequestURI();
        String method = req.getMethod();
        if ("OPTIONS".equals(method)) return true;

        HttpSession session = req.getSession(false);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = session == null ? null : (Map<String, Object>) session.getAttribute(SESSION_KEY);
        if (user == null) return deny(res, 401, "ログインが必要です。");

        String role = String.valueOf(user.get("role"));
        boolean admin = "ADMIN".equals(role);
        boolean staff = STAFF.contains(role);
        boolean write = !"GET".equals(method);

        // 管理者専用: ユーザー管理・部署/勤務形態の変更・ログ・開発用時刻
        if (path.startsWith("/api/users") || path.startsWith("/api/admin") || path.startsWith("/api/system/dev-time")
                || (write && (path.startsWith("/api/departments") || path.startsWith("/api/worktypes")))) {
            if (!admin) return deny(res, 403, "管理者のみ利用できます。");
        }
        // 管理職・人事・管理者: 承認・部下状況
        if (path.startsWith("/api/manager") || path.startsWith("/api/leave/pending") || path.startsWith("/api/leave/approve")) {
            if (!staff) return deny(res, 403, "権限がありません。");
        }
        // 一般社員は他人の userId を指定できない
        String uid = req.getParameter("userId");
        if (uid != null && !staff && !uid.equals(String.valueOf(user.get("userId")))) {
            return deny(res, 403, "他のユーザーのデータにはアクセスできません。");
        }
        return true;
    }

    private boolean deny(HttpServletResponse res, int status, String message) throws Exception {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
        return false;
    }
}
