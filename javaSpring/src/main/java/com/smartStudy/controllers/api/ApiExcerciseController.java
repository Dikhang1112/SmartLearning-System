package com.smartStudy.controllers.api;

import com.smartStudy.dto.ChapterDTO;
import com.smartStudy.dto.ExcerciseDTO;
import com.smartStudy.dto.SubjectDTO;
import com.smartStudy.pojo.Chapter;
import com.smartStudy.pojo.Exercise;
import com.smartStudy.pojo.Subject;
import com.smartStudy.pojo.Teacher; // nếu cần
import com.smartStudy.services.ExcerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiExcerciseController {

    @Autowired
    private ExcerciseService excerciseService;

    @GetMapping("/excercises")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        List<Exercise> items = excerciseService.getExercises(params);
        long total = excerciseService.countExercises(params);

        List<ExcerciseDTO> dtos = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", total);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/excercises/{excerciseId}")
    public ResponseEntity<?> get(@PathVariable (value = "excerciseId") Integer id) {
        Exercise ex = excerciseService.get(id);
        if (ex == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(ex));
    }

    @GetMapping("/exercises/chapter/{chapterId}")
    public ResponseEntity<?> getExcerciseByChapter(@PathVariable (value = "chapterId") Integer chapterId) {
        List<Exercise> items = excerciseService.findByChapterId(chapterId);
        List<ExcerciseDTO> dtos = items.stream().map(this::toDto).collect(Collectors.toList());
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", dtos);
        payload.put("total", dtos.size());
        return ResponseEntity.ok(payload);
    }
    //=====  CREATE  =====
    @PostMapping("/excercises")
    public ResponseEntity<?> create(@RequestBody Exercise req) {
        // req có thể chứa chapterId (embedded object với id) và createdBy (Teacher) như JSON bạn đang dùng
        Exercise created = excerciseService.create(req);
        return ResponseEntity.ok(toDto(created));
    }

    // ====== UPDATE ======
    @PutMapping("/excercises/{id}")
    public ResponseEntity<?> update(@PathVariable (value = "id") Integer id, @RequestBody Exercise req) {
        Exercise updated = excerciseService.update(id, req);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(updated));
    }

    // ====== DELETE ======
    @DeleteMapping("/excercises/{id}")
    public ResponseEntity<?> delete(@PathVariable (value = "id") Integer id) {
        excerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // ----------------- helpers -----------------

    private ExcerciseDTO toDto(Exercise e) {
        // createdBy: chỉ userId
        Integer createdByUserId = null;
        if (e.getCreatedBy() != null) {
            // Nếu Teacher có getUserId():
            try {
                createdByUserId = (Integer) e.getCreatedBy().getClass().getMethod("getUserId").invoke(e.getCreatedBy());
            } catch (Exception ignore) {
                // fallback: nếu Teacher dùng id làm userId
                try {
                    createdByUserId = (Integer) e.getCreatedBy().getClass().getMethod("getId").invoke(e.getCreatedBy());
                } catch (Exception ignored) { /* no-op */ }
            }
        }

        ChapterDTO chapterDTO = null;
        Chapter chapter = e.getChapterId(); // tên getter đúng theo entity của bạn
        if (chapter != null) {
            SubjectDTO subjectDTO = null;

            // lấy Subject từ Chapter
            Subject subject = null;
            try {
                // tuỳ entity: getSubjectId() hoặc getSubject()
                subject = (Subject) chapter.getClass().getMethod("getSubjectId").invoke(chapter);
            } catch (Exception ex) {
                try {
                    subject = (Subject) chapter.getClass().getMethod("getSubject").invoke(chapter);
                } catch (Exception ignored) { /* no-op */ }
            }

            if (subject != null) {
                subjectDTO = new SubjectDTO(
                        subject.getId(),
                        subject.getTitle(),
                        subject.getImage(),
                        subject.getTeacherNames()
                );
            }

            chapterDTO = new ChapterDTO(
                    chapter.getId(),
                    chapter.getOrderIndex(),
                    chapter.getTitle(),
                    subjectDTO
            );
        }

        return new ExcerciseDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getType(),
                createdByUserId,
                chapterDTO
        );
    }
}
