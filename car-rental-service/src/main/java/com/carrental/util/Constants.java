package com.carrental.util;

public class Constants {
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    // File Paths
    public static final String VEHICLE_IMAGES_PATH = "vehicles";
    public static final String DRIVER_DOCUMENTS_PATH = "drivers";
    public static final String DAMAGE_IMAGES_PATH = "damage";
    public static final String PROFILE_IMAGES_PATH = "profiles";
    
    // Default Values
    public static final double DEFAULT_COMMISSION_PERCENTAGE = 20.0;
    public static final int DEFAULT_MIN_RENTAL_HOURS = 1;
    public static final int DEFAULT_MAX_RENTAL_DAYS = 30;
    public static final int FREE_CANCELLATION_HOURS = 48;
    public static final double CANCELLATION_PENALTY_PERCENTAGE = 20.0;
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    
    // Cache Names
    public static final String VEHICLES_CACHE = "vehicles";
    public static final String USERS_CACHE = "users";
    public static final String OFFERS_CACHE = "offers";
    public static final String LOCATIONS_CACHE = "locations";
    
    // Security
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String USER_ID_HEADER = "X-User-Id";
    
    // Error Messages
    public static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_VALIDATION_FAILED = "Validation failed";
    public static final String ERROR_INTERNAL_SERVER = "Internal server error";
    
    // Success Messages
    public static final String SUCCESS_CREATED = "Created successfully";
    public static final String SUCCESS_UPDATED = "Updated successfully";
    public static final String SUCCESS_DELETED = "Deleted successfully";
    public static final String SUCCESS_RETRIEVED = "Retrieved successfully";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}