package com.smart.dao;

import com.smart.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Репозиторий для работы с таблицей CONTACT.
 * Наследуется от JpaRepository — поэтому автоматически получает методы:
 * save(), findById(), delete(), findAll(), count() и т.д.
 */
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("from Contact c where c.user.id = :userId")
    Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

    @Query("from Contact c " +
            "where c.user.id = :userId and " +
            "(:hasPhone = false or (c.phone is not null and c.phone <> '')) and " +
            "(:hasEmail = false or (c.email is not null and c.email <> ''))")
    Page<Contact> findFilteredByUser(@Param("userId") int userId,
                                     @Param("hasPhone") boolean hasPhone,
                                     @Param("hasEmail") boolean hasEmail,
                                     Pageable pageable);

    @Query("from Contact c " +
            "where c.user.id = :userId and " +
            "(" +
            " lower(c.name)   like lower(concat('%', :keyword, '%')) or " +
            " lower(c.email)  like lower(concat('%', :keyword, '%')) or " +
            " lower(c.phone)  like lower(concat('%', :keyword, '%')) or " +
            " lower(c.work)   like lower(concat('%', :keyword, '%')) " +
            ")")
    Page<Contact> searchContactsByUser(@Param("userId") int userId,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);
}



