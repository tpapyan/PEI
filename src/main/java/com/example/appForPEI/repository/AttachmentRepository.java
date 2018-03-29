package com.example.appForPEI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.appForPEI.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer>{
}
