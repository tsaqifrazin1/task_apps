package test.samir.be.app.task;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import test.samir.be.app.user.User;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {
    @EntityGraph(attributePaths = "tags")
    Task save(Task task);
    Optional<Task> findFirstByIdAndUser(String id, User user);
}
