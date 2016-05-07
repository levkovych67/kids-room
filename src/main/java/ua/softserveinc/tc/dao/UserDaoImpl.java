package ua.softserveinc.tc.dao;

import org.springframework.stereotype.Repository;
import ua.softserveinc.tc.entity.ColumnConstants.UserConst;
import ua.softserveinc.tc.entity.User;

import javax.persistence.TypedQuery;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

    @Override
    public User getUserByEmail(String email) {
        TypedQuery<User> query = getEntityManager().createNamedQuery(User.NQ_FIND_USER_BY_EMAIL, User.class);
        return query.setParameter(UserConst.EMAIL, email).getSingleResult();
    }
}
