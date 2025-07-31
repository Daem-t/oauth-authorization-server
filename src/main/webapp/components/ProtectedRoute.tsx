import React from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {Spin} from 'antd';
import {useAuth} from '../hooks/useAuth'; // 引入 useAuth

interface ProtectedRouteProps {
    children: React.ReactNode;
    requireAuth?: boolean;
    requiredRoles?: string[];
}

/**
 * 路由守卫组件
 * - requireAuth=true (默认): 需要登录才能访问。未登录则重定向到/login。
 * - requireAuth=false: 不需要登录就能访问。如果已登录，则重定向到/dashboard。
 * - requiredRoles: 需要特定角色才能访问。
 */
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
                                                           children,
                                                           requireAuth = true,
                                                           requiredRoles = []
                                                       }) => {
    const {isLoggedIn, user, isLoading} = useAuth();
    const location = useLocation();

    if (isLoading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '200px'
            }}>
                <Spin size="large"/>
            </div>
        );
    }

    // 场景1: 路由需要认证
    if (requireAuth) {
        // 1a: 用户未登录，重定向到登录页
        if (!isLoggedIn) {
            const returnUrl = location.pathname + location.search;
            return <Navigate to={`/login?return_url=${encodeURIComponent(returnUrl)}`} replace/>;
        }

        // 1b: 需要特定角色，但用户角色不匹配
        if (requiredRoles.length > 0 && !requiredRoles.some(role => user?.roles.includes(role))) {
            // 可以重定向到 403 Forbidden 页面
            return <Navigate to="/dashboard" replace/>; // 或者专门的 403 页面
        }

        // 1c: 认证和授权都通过
        return <>{children}</>;
    }

    // 场景2: 路由不需要认证 (例如 /login, /register)
    // 如果用户已经登录，则重定向到仪表盘
    if (isLoggedIn) {
        return <Navigate to="/dashboard" replace/>;
    }

    // 用户未登录，可以访问该页面
    return <>{children}</>;
};

export default ProtectedRoute;
