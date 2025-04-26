package test.samir.be.app.tags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import test.samir.be.app.user.User;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
    Optional<Tag> findFirstByNameAndUser(String name, User user);
}
