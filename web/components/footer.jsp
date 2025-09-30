<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 
    Warehouse Management System - Footer Component
    Reusable footer for all pages in the application
-->

<footer class="app-footer">
    <div class="footer-container">
        <div class="footer-left">
            <a href="${pageContext.request.contextPath}/" class="brand-link">
                <svg class="brand-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                    <span class="brand-name">Warehouse MS</span>
            </a>
            <span class="text-muted">&copy; 2025. All rights reserved.</span>
        </div>
        <div class="footer-right">
            <ul class="footer-links">
                <li><a href="${pageContext.request.contextPath}/about">About</a></li>
                <li><a href="${pageContext.request.contextPath}/help">Help</a></li>
                <li><a href="${pageContext.request.contextPath}/privacy">Privacy</a></li>
                <li><a href="${pageContext.request.contextPath}/terms">Terms</a></li>
            </ul>
        </div>
    </div>
</footer>
