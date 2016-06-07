package ua.softserveinc.tc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.softserveinc.tc.constants.BookingConstants;
import ua.softserveinc.tc.dao.BookingDao;
import ua.softserveinc.tc.dao.UserDao;
import ua.softserveinc.tc.entity.*;
import ua.softserveinc.tc.server.exception.ResourceNotFoundException;
import ua.softserveinc.tc.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static ua.softserveinc.tc.util.DateUtil.toDate;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getActiveUsers(String startDate, String endDate, Room room) {
        EntityManager entityManager = bookingDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<Booking> root = query.from(Booking.class);

        query.select(root.get(BookingConstants.Entity.USER)).distinct(true).where(
                builder.between(root.get(BookingConstants.Entity.START_TIME),
                        toDate(startDate), toDate(endDate)),
                builder.equal(root.get(BookingConstants.Entity.STATE), BookingState.COMPLETED),
                builder.equal(root.get(BookingConstants.Entity.ROOM), room));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> findAllUsersByRole(Role role) {
        return userDao.findAllUsersByRole(role);
    }

    @Override
    public void deleteUserById(Long id) {
        userDao.deleteUserById(id);
    }

    @Override
    public User getUserByEmail(String email) throws ResourceNotFoundException {
        User user = userDao.getUserByEmail(email);
        if (user == null)
            throw new ResourceNotFoundException();
        return user;
    }

    @Override
    public void create(User user) {
        userDao.create(user);
    }

    @Override
    public void createWithEncoder(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.create(user);
    }

    @Override
    public void confirmManagerRegistrationUpdate(User manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        userDao.update(manager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllParents() {
        EntityManager entityManager = userDao.getEntityManager();
        List<User> list = (List<User>) entityManager
                .createQuery("from User" +
                        " where role = 0")
                .getResultList();
        return list;
    }

}
