package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import jm.task.core.jdbc.util.Util.Action;
import org.hibernate.Session;

import java.util.ArrayList;

public class UserDaoHibernateImpl implements UserDao {
    private ArrayList<User> allUsers = new ArrayList<>();

    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        queryByAction(Action.CREATEUSERSTABLE, Action.NULLPARAMS);
    }

    @Override
    public void dropUsersTable() {
        queryByAction(Action.DROPUSERSTABLE, Action.NULLPARAMS);
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        User user = new User(name, lastName, age);
        queryByAction(Action.SAVEUSER, user);
    }

    @Override
    public void removeUserById(long id) {
        queryByAction(Action.REMOVEUSERBYID, id);
    }

    @Override
    public ArrayList<User> getAllUsers() {
        queryByAction(Action.GETALLUSERS, Action.NULLPARAMS);
        return allUsers;
    }

    @Override
    public void cleanUsersTable() {
        queryByAction(Action.CLEANUSERSTABLE, Action.NULLPARAMS);
    }

    private void queryByAction(Action action, Object actionUserParam) {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            try {
                routeByAction(session, action, actionUserParam);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
    }

    private void routeByAction(Session session, Action action, Object actionUserParam) {
        switch (action) {
            case SAVEUSER -> session.save(actionUserParam);
            case CLEANUSERSTABLE -> session.createQuery(action.getQuery()).executeUpdate();
            case REMOVEUSERBYID -> session.delete(session.get(User.class, (long) actionUserParam));
            case DROPUSERSTABLE, CREATEUSERSTABLE -> session.createNativeQuery(action.getQuery()).executeUpdate();
            case GETALLUSERS -> allUsers = (ArrayList<User>) session.createQuery(action.getQuery(), User.class).list();
        }
    }
}
