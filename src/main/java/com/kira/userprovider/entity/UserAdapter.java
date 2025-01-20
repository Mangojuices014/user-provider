package com.kira.userprovider.entity;

import lombok.RequiredArgsConstructor;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {

    private final String id;
    private final User user;
    private final Connection connection;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, User user, Connection connection) {
        super(session, realm, storageProviderModel);
        this.user = user;
        this.id = StorageId.keycloakId(storageProviderModel, user.getId().toString());
        this.connection = connection;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String s) {
        user.setUsername(s);
    }

    @Override
    public String getId(){
        return id;
    }

    @Override
    public String getEmail(){
        return user.getEmail();
    }

    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        updateDatabase("email", email);
    }

    @Override
    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
        updateDatabase("firstname", firstName);
    }

    @Override
    public void setLastName(String lastName) {
        super.setLastName(lastName);
        updateDatabase("lastname", lastName);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        super.setAttribute(name, values);
        if ("firstName".equals(name)) {
            updateDatabase("firstname", values.getFirst());
        } else if ("lastName".equals(name)) {
            updateDatabase("lastname", values.getFirst());
        } else if ("email".equals(name)) {
            updateDatabase("email", values.getFirst());
        }
    }

    private void updateDatabase(String field, String value) {
        String query = "UPDATE users SET " + field + " = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setLong(2, Long.parseLong(StorageId.externalId(getId())));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
