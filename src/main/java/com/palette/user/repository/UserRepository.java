package com.palette.user.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM User u WHERE u.email = :email", nativeQuery = true)
    Optional<User> findUsersIncDeletedUserByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM History h " +
            "RIGHT JOIN Diary d ON h.diary = d AND h = :history " +
            "RIGHT JOIN DiaryGroup g ON g.diary = d AND g.isOuted = false " +
            "RIGHT JOIN User u ON u = g.user"
    )
    List<User> findUsers(@Param("history") History history);

//    @Query(value = "SELECT u FROM Page p " +
//            "RIGHT JOIN History h ON h = p.history AND p.page_id = :pageId " +
//            "RIGHT JOIN Diary d ON d = h.diary " +
//            "RIGHT JOIN DiaryGroup g ON g.diary = d " +
//            "RIGHT JOIN User u ON u = g.user"
//    )
//    List<User> findUsers(@Param("pageId") Long pageId);

    @Query(value = "SELECT u FROM Page p " +
            "RIGHT JOIN History h ON h = p.history AND p = :page " +
            "RIGHT JOIN Diary d ON d = h.diary " +
            "RIGHT JOIN DiaryGroup g ON g.diary = d " +
            "RIGHT JOIN User u ON u = g.user"
    )
    List<User> findUsers(@Param("page") Page page);
}
