package com.projectgloriam.parkingservice.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String id;
    private String displayName;
    private String email;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String id, String displayName, String email) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
    }

    String getId() {
        return id;
    }
    String getDisplayName() {
        return displayName;
    }
    String getEmail() {
        return email;
    }
}