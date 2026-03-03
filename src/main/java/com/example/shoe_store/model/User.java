package com.example.shoe_store.model;

public record User(int userId, String fullName, String login, String password, String role) {


    public boolean isGuest() {
        return "Гость".equalsIgnoreCase(this.role);
    }

    public boolean isAdmin() {
        return "Администратор".equalsIgnoreCase(this.role);
    }

    public boolean isManager() {
        return "Менеджер".equalsIgnoreCase(this.role);
    }

    public boolean isClient() {
        return "Авторизованный клиент".equalsIgnoreCase(this.role);
    }
}

