package mezyk.mateusz.app.tasks.core.data.repository;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByCompletedFalse();

    List<Task> findAllByCompletedTrue();

}
