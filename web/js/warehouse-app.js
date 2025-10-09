/**
 * Warehouse Management System - Main JavaScript
 * Handles interactive components and UI behaviors
 */

(function() {
    'use strict';
    
    // Wait for DOM to be ready
    document.addEventListener('DOMContentLoaded', function() {
        initDropdowns();
        initMobileMenu();
        initSidebarNav();
        initNotifications();
        highlightActiveNav();
    });
    
    /**
     * Initialize dropdown menus
     */
    function initDropdowns() {
        const dropdowns = document.querySelectorAll('.nav-item.dropdown, .user-menu');
        
        dropdowns.forEach(function(dropdown) {
            const link = dropdown.querySelector('.nav-link, .user-btn');
            const menu = dropdown.querySelector('.dropdown-menu');
            
            if (!link || !menu) return;
            
            // Toggle dropdown on click (for mobile/touch devices)
            link.addEventListener('click', function(e) {
                if (window.innerWidth <= 768) {
                    e.preventDefault();
                    dropdown.classList.toggle('active');
                }
            });
            
            // Close dropdown when clicking outside
            document.addEventListener('click', function(e) {
                if (!dropdown.contains(e.target)) {
                    dropdown.classList.remove('active');
                }
            });
        });
    }
    
    /**
     * Initialize mobile menu toggle
     */
    function initMobileMenu() {
        const mobileToggle = document.getElementById('mobileMenuToggle');
        const headerNav = document.querySelector('.header-nav');

        if (!mobileToggle || !headerNav) return;

        mobileToggle.addEventListener('click', function() {
            headerNav.classList.toggle('mobile-active');
            document.body.classList.toggle('menu-open');
        });

        // Close menu when clicking outside
        document.addEventListener('click', function(e) {
            if (!headerNav.contains(e.target) && !mobileToggle.contains(e.target)) {
                headerNav.classList.remove('mobile-active');
                document.body.classList.remove('menu-open');
            }
        });
    }

    /**
     * Initialize sidebar navigation
     */
    function initSidebarNav() {
        const sidebarToggle = document.getElementById('sidebarToggle');
        const sidebar = document.querySelector('.sidebar-nav');
        const sidebarDropdowns = document.querySelectorAll('.sidebar-dropdown');

        // Toggle sidebar on mobile
        if (sidebarToggle && sidebar) {
            sidebarToggle.addEventListener('click', function() {
                sidebar.classList.toggle('active');
            });

            // Close sidebar when clicking outside on mobile
            document.addEventListener('click', function(e) {
                if (window.innerWidth <= 767) {
                    if (!sidebar.contains(e.target) && !sidebarToggle.contains(e.target)) {
                        sidebar.classList.remove('active');
                    }
                }
            });
        }

        // Handle sidebar dropdown toggles
        sidebarDropdowns.forEach(function(dropdown) {
            const toggle = dropdown.querySelector('.sidebar-toggle');

            if (!toggle) return;

            toggle.addEventListener('click', function(e) {
                e.preventDefault();
                dropdown.classList.toggle('active');

                // Close other dropdowns
                sidebarDropdowns.forEach(function(otherDropdown) {
                    if (otherDropdown !== dropdown) {
                        otherDropdown.classList.remove('active');
                    }
                });
            });
        });

        // Note: Sidebar highlighting is now handled by JSP/JSTL using activePage variable
        // This ensures precise matching without URL substring conflicts

        // Expand dropdown if it contains an active link (set by JSP)
        // Use setTimeout to ensure this runs after JSP rendering
        setTimeout(function() {
            const activeSublinks = document.querySelectorAll('.sidebar-sublink.active');
            activeSublinks.forEach(function(activeLink) {
                const parentDropdown = activeLink.closest('.sidebar-dropdown');
                if (parentDropdown) {
                    parentDropdown.classList.add('active');
                }
            });
        }, 50);
    }
    
    /**
     * Initialize notification system
     */
    function initNotifications() {
        const notificationBtn = document.querySelector('.action-btn[title="Notifications"]');
        
        if (!notificationBtn) return;
        
        notificationBtn.addEventListener('click', function() {
            // TODO: Implement notification panel
            console.log('Notifications clicked');
        });
    }
    
    /**
     * Highlight active navigation item based on current URL
     */
    function highlightActiveNav() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link, .dropdown-menu a');
        
        navLinks.forEach(function(link) {
            const href = link.getAttribute('href');
            
            if (href && currentPath.includes(href) && href !== '/') {
                link.classList.add('active');
                
                // If it's a dropdown item, also highlight the parent
                const parentDropdown = link.closest('.nav-item.dropdown');
                if (parentDropdown) {
                    const parentLink = parentDropdown.querySelector('.nav-link');
                    if (parentLink) {
                        parentLink.classList.add('active');
                    }
                }
            }
        });
    }
    
    /**
     * Utility: Show toast notification
     */
    window.showToast = function(message, type) {
        type = type || 'info';
        
        const toast = document.createElement('div');
        toast.className = 'toast toast-' + type;
        toast.textContent = message;
        
        document.body.appendChild(toast);
        
        // Trigger animation
        setTimeout(function() {
            toast.classList.add('show');
        }, 10);
        
        // Remove after 3 seconds
        setTimeout(function() {
            toast.classList.remove('show');
            setTimeout(function() {
                document.body.removeChild(toast);
            }, 300);
        }, 3000);
    };
    
    /**
     * Utility: Confirm dialog
     */
    window.confirmAction = function(message, callback) {
        if (confirm(message)) {
            callback();
        }
    };
    
    /**
     * Utility: Format currency
     */
    window.formatCurrency = function(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };
    
    /**
     * Utility: Format date
     */
    window.formatDate = function(date) {
        return new Intl.DateTimeFormat('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        }).format(new Date(date));
    };
    
})();

