<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!--
    Warehouse Management System - Top Header Component
    Simple top header with branding and user actions
-->

<header class="top-header">
    <div class="top-header-container">
        <!-- Logo and Brand -->
        <div class="top-header-brand">
            <a href="${pageContext.request.contextPath}/" class="brand-link">
                <svg class="brand-icon" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                <div class="brand-text">
                    <span class="brand-name">Warehouse MS</span>
                    <span class="brand-subtitle">Management System</span>
                </div>
            </a>
        </div>

        <!-- Spacer -->
        <div class="top-header-spacer"></div>

        <!-- User Profile & Actions -->
        <div class="top-header-actions">
            <!-- Notifications -->
            <div class="header-action-item">
                <button class="action-btn" title="Notifications">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                    </svg>
                    <span class="notification-badge">3</span>
                </button>
            </div>

        <!-- User Menu -->
        <div class="header-action-item user-menu">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <button class="user-btn">
                            <div class="user-avatar">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.avatar}">
                                        <img src="${pageContext.request.contextPath}/${sessionScope.user.avatar}" alt="${sessionScope.user.fullname}">
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="firstLetter" value="${fn:substring(sessionScope.user.fullname, 0, 1)}" />
                                        <span class="avatar-placeholder">${firstLetter}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="user-info">
                                <span class="user-name">${sessionScope.user.fullname}</span>
                                <span class="user-role">${sessionScope.user.role}</span>
                            </div>
                            <svg class="dropdown-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6 9 12 15 18 9"></polyline>
                            </svg>
                        </button>
                        <ul class="dropdown-menu user-dropdown">
                            <li><a href="${pageContext.request.contextPath}/profile">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                My Profile
                            </a></li>
                            <li><a href="${pageContext.request.contextPath}/account">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="3"></circle>
                                    <path d="M12 1v6m0 6v6m5.2-13.2l-4.2 4.2m0 6l4.2 4.2M23 12h-6m-6 0H1m18.2 5.2l-4.2-4.2m0-6l4.2-4.2"></path>
                                </svg>
                                Account Settings
                            </a></li>
                            <li class="divider"></li>
                            <li><a href="${pageContext.request.contextPath}/logout" class="logout-link">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                    <polyline points="16 17 21 12 16 7"></polyline>
                                    <line x1="21" y1="12" x2="9" y2="12"></line>
                                </svg>
                                Logout
                            </a></li>
                        </ul>
                </c:when>
                     <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Login</a>
                    </c:otherwise>
            </c:choose>
            </div>
        </div>
    </div>
</header>

