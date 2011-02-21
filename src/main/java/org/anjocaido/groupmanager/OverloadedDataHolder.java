/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author gabrielcouto
 */
public class OverloadedDataHolder extends DataHolder {

    /**
     *
     */
    protected Map<String, User> overloadedUsers = new HashMap<String, User>();

    /**
     *
     * @param ph
     */
    public OverloadedDataHolder(DataHolder ph) {
        this.f = ph.f;
        this.defaultGroup = ph.defaultGroup;
        this.groups = ph.groups;
        this.users = ph.users;
    }

    /**
     *
     * @param userName
     * @return
     */
    @Override
    public User getUser(String userName) {
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(userName.toLowerCase())) {
            return overloadedUsers.get(userName.toLowerCase());
        }
        //END CODE
        if (users.containsKey(userName.toLowerCase())) {
            return users.get(userName.toLowerCase());
        }
        User newUser = createUser(userName);
        return newUser;
    }

    /**
     *
     * @param theUser
     */
    @Override
    public void addUser(User theUser) {
        if (theUser.getDataSource() != this) {
            theUser = theUser.clone(this);
        }
        if (theUser == null) {
            return;
        }
        if ((theUser.getGroup() == null) || (!groups.containsKey(theUser.getGroupName()))) {
            theUser.setGroup(defaultGroup);
        }
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(theUser.getName())) {
            overloadedUsers.remove(theUser.getName());
            overloadedUsers.put(theUser.getName().toLowerCase(), theUser);
            return;
        }
        //END CODE
        removeUser(theUser.getName());
        users.put(theUser.getName().toLowerCase(), theUser);
    }

    /**
     *
     * @param userName
     * @return
     */
    @Override
    public boolean removeUser(String userName) {
        //OVERLOADED CODE
        if (overloadedUsers.containsKey(userName.toLowerCase())) {
            overloadedUsers.remove(userName.toLowerCase());
            return true;
        }
        //END CODE
        if (users.containsKey(userName.toLowerCase())) {
            users.remove(userName.toLowerCase());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeGroup(String groupName) {
        if (groupName.equals(defaultGroup)) {
            return false;
        }
        for (String key : groups.keySet()) {
            if (groupName.equalsIgnoreCase(key)) {
                groups.remove(key);
                for (String userKey : users.keySet()) {
                    User user = users.get(userKey);
                    if (user.getGroupName().equalsIgnoreCase(key)) {
                        user.setGroup(defaultGroup);
                    }

                }
                //OVERLOADED CODE
                for (String userKey : overloadedUsers.keySet()) {
                    User user = overloadedUsers.get(userKey);
                    if (user.getGroupName().equalsIgnoreCase(key)) {
                        user.setGroup(defaultGroup);
                    }

                }
                //END OVERLOAD
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return
     */
    @Override
    public Collection<User> getUserList() {
        Collection<User> overloadedList = new ArrayList<User>();
        Collection<User> normalList = users.values();
        for (User u : normalList) {
            if (overloadedUsers.containsKey(u.getName())) {
                overloadedList.add(overloadedUsers.get(u.getName()));
            } else {
                overloadedList.add(u);
            }
        }
        return overloadedList;
    }

    /**
     *
     * @param userName
     * @return
     */
    public boolean isOverloaded(String userName) {
        return overloadedUsers.containsKey(userName.toLowerCase());
    }

    /**
     *
     * @param userName
     */
    public void overloadUser(String userName) {
        if (!isOverloaded(userName)) {
            User theUser = getUser(userName);
            theUser = theUser.clone();
            if (overloadedUsers.containsKey(theUser.getName())) {
                overloadedUsers.remove(theUser.getName());
            }
            overloadedUsers.put(theUser.getName(), theUser);
        }
    }

    /**
     *
     * @param userName
     */
    public void removeOverload(String userName) {
        //System.out.println("Grupo antes " + this.getUser(userName).group.getName());
        overloadedUsers.remove(userName.toLowerCase());
        //System.out.println("Grupo depois " + this.getUser(userName).group.getName());
    }

    /**
     *  Gets the user in normal state. Surpassing the overload state.
     * It doesn't affect permissions. But it enables plugins change the
     * actual user permissions even in overload mode.
     * @param userName
     * @return
     */
    public User surpassOverload(String userName) {
        if (!isOverloaded(userName)) {
            return getUser(userName);
        }
        if (users.containsKey(userName.toLowerCase())) {
            return users.get(userName.toLowerCase());
        }
        User newUser = createUser(userName);
        return newUser;
    }
}
