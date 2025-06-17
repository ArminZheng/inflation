package com.arminzheng.inflation.repository;

import com.arminzheng.inflation.model.UserPO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserPO, Long> {

    UserPO findByEmail(String email);

    List<UserPO> findByNameContaining(String name);

    boolean existsByEmail(String email);
}
