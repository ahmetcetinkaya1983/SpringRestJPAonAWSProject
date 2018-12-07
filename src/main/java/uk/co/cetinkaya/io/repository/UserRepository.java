package uk.co.cetinkaya.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.co.cetinkaya.io.entity.UserEntity;

/*
 * public interface UserRepository extends CrudRepository<UserEntity, Long>
 *  									and PagingAndSortingRepository<UserEntity, Long>
 */

@Repository  
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>{
	
	UserEntity findByEmail(String email);

	UserEntity findByUserId(String userId);
}
