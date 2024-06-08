package  com.securitydemo.jwt_security_demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.securitydemo.jwt_security_demo.entity.*;;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer>{

    List<Transaction> findAllByUsername(String username);

} 
