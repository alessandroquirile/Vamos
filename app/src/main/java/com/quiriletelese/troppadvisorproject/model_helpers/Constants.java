package com.quiriletelese.troppadvisorproject.model_helpers;

public abstract class Constants {

    /* ========== Permission utils ========== */
    private static final int accessFineLocationCode = 100;

    /* ========== Storage technology utils ========== */
    private static final String hotelStorageTechnology = "hotel_storage_technology";
    private static final String restaurantStorageTechnology = "restaurant_storage_technology";
    private static final String attractionStorageTechnology = "attraction_storage_technology";
    private static final String cityStorageTechnology = "city_storage_technology";
    private static final String accountStorageTechnology = "account_storage_technology";
    private static final String reviewStorageTechnology = "review_storage_technology";
    private static final String typesOfCuisineStorageTechnology = "types_of_cuisine_storage_technology";
    private static final String userStorageTechnology = "user_storage_technology";
    private static final String s3StorageTechnology = "s3storage_technology";

    /* ========== Accomodation utils ========== */
    private static final String id = "id";
    private static final String accomodationType = "accomodation_type";
    private static final String accomodationName = "accomodation_name";
    private static final String accomodation = "accomodation";
    private static final String hotel = "hotel";
    private static final String restaurant = "restaurant";
    private static final String attraction = "attraction";
    private static final String pointSearch = "point_search";
    private static final String rsqlQuery = "rsql_query";
    private static final String searchForName = "search_for_name";
    private static final String name = "name";
    private static final String accomodationFilter = "accomodation_filter";

    /* ========== SharedPreferences utils ========== */
    private static final String sharedPreferences = "shared_preferences";
    private static final String username = "username";
    private static final String familyName = "family_name";
    private static final String userFirstName = "user_first_name";
    private static final String email = "email";
    private static final String accessToken = "access_token";
    private static final String idToken = "id_token";
    private static final String refreshToken = "refresh_token";
    private static final String isAppOpenedForFirstTime = "is_app_opened_for_first_time";
    private static final String dailyReward = "daily_reward";

    /* ========== Volley utils ========== */
    private static final String baseUrl = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/";
    private static final String baseUrlHttps = "https://5il6dxqqm3.execute-api.us-east-1.amazonaws.com/Secondo/";
    private static final String userRoute = "user/";
    private static final String findUserByEmailRoute = "find-by-email/";
    private static final String findUsersByNameOrUsermaneRoute = "find-by-name-or-username/";
    private static final String findLeaderboardUser = "find-leaderboard";
    private static final String updateUserInformationsRoute = "update-user-informations";
    private static final String updateUserImageRoute = "update-user-image/";
    private static final String s3Route = "s3/";
    private static final String s3UploadFileRoute = "upload-file";
    private static final String s3DeleteFileRoute = "delete-file";
    private static final String cognitoRoute = "cognito/";
    private static final String sendConfirmationCodeRoute = "forgot-password/";
    private static final String changePassworRoute = "confirm-forgot-password?";
    private static final String usernameError = "Username error";
    private static final String alreadyVotedError = "voted";
    private static final String emailError = "Email error";
    private static final String noContent = "204";
    private static final String unauthorized = "401";
    private static final String internalServerError = "500";

    /* ========== Select image utils ========== */
    private static final int selectPictureCode = 200;
    private static final String savedProfileImagePath = "saved_profile_image_path";

    public static int getAccessFineLocationCode() {
        return accessFineLocationCode;
    }

    public static String getHotelStorageTechnology() {
        return hotelStorageTechnology;
    }

    public static String getRestaurantStorageTechnology() {
        return restaurantStorageTechnology;
    }

    public static String getAttractionStorageTechnology() {
        return attractionStorageTechnology;
    }

    public static String getCityStorageTechnology() {
        return cityStorageTechnology;
    }

    public static String getAccountStorageTechnology() {
        return accountStorageTechnology;
    }

    public static String getReviewStorageTechnology() {
        return reviewStorageTechnology;
    }

    public static String getTypesOfCuisineStorageTechnology() {
        return typesOfCuisineStorageTechnology;
    }

    public static String getUserStorageTechnology() {
        return userStorageTechnology;
    }

    public static String getS3StorageTechnology() {
        return s3StorageTechnology;
    }

    public static String getId() {
        return id;
    }

    public static String getAccomodationType() {
        return accomodationType;
    }

    public static String getAccomodationName() {
        return accomodationName;
    }

    public static String getAccomodation() {
        return accomodation;
    }

    public static String getHotel() {
        return hotel;
    }

    public static String getRestaurant() {
        return restaurant;
    }

    public static String getAttraction() {
        return attraction;
    }

    public static String getPointSearch() {
        return pointSearch;
    }

    public static String getRsqlQuery() {
        return rsqlQuery;
    }

    public static String getSearchForName() {
        return searchForName;
    }

    public static String getName() {
        return name;
    }

    public static String getAccomodationFilter() {
        return accomodationFilter;
    }

    public static String getSharedPreferences() {
        return sharedPreferences;
    }

    public static String getUsername() {
        return username;
    }

    public static String getFamilyName() {
        return familyName;
    }

    public static String getUserFirstName() {
        return userFirstName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static String getIdToken() {
        return idToken;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getIsAppOpenedForFirstTime() {
        return isAppOpenedForFirstTime;
    }

    public static String getDailyReward() {
        return dailyReward;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getBaseUrlHttps() {
        return baseUrlHttps;
    }

    public static String getUserRoute() {
        return userRoute;
    }

    public static String getFindUserByEmailRoute() {
        return findUserByEmailRoute;
    }

    public static String getFindUsersByNameOrUsermaneRoute() {
        return findUsersByNameOrUsermaneRoute;
    }

    public static String getFindLeaderboardUser() {
        return findLeaderboardUser;
    }

    public static String getUpdateUserInformationsRoute() {
        return updateUserInformationsRoute;
    }

    public static String getUpdateUserImageRoute() {
        return updateUserImageRoute;
    }

    public static String getS3Route() {
        return s3Route;
    }

    public static String getS3UploadFileRoute() {
        return s3UploadFileRoute;
    }

    public static String getS3DeleteFileRoute() {
        return s3DeleteFileRoute;
    }

    public static String getCognitoRoute() {
        return cognitoRoute;
    }

    public static String getSendConfirmationCodeRoute() {
        return sendConfirmationCodeRoute;
    }

    public static String getChangePassworRoute() {
        return changePassworRoute;
    }

    public static String getUsernameError() {
        return usernameError;
    }

    public static String getAlreadyVotedError() {
        return alreadyVotedError;
    }

    public static String getEmailError() {
        return emailError;
    }

    public static String getNoContent() {
        return noContent;
    }

    public static String getUnauthorized() {
        return unauthorized;
    }

    public static String getInternalServerError() {
        return internalServerError;
    }

    public static int getSelectPictureCode() {
        return selectPictureCode;
    }

    public static String getSavedProfileImagePath() {
        return savedProfileImagePath;
    }
}
