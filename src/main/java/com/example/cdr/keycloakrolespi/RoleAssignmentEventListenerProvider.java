package com.example.cdr.keycloakrolespi;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;


public class RoleAssignmentEventListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;

    public RoleAssignmentEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            String userId = event.getUserId();
            RealmModel realm = session.getContext().getRealm();
            UserModel user = session.users().getUserById(realm, userId);

            if (user == null) return;

            // Get role from field "userType"
            String selectedRoleName = user.getFirstAttribute("userType");

            if (selectedRoleName == null || selectedRoleName.isEmpty()) {
                System.out.println("No userType attribute found for user: " + user.getUsername());
                return;
            }

            RoleModel role = realm.getRole(selectedRoleName);

            if (role != null) {
                user.grantRole(role);
                System.out.println("Assigned role: " + role + " to user: " + user.getUsername());
            } else {
                System.out.println("Role " + role + " not found in realm " + realm.getName());
            }
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
