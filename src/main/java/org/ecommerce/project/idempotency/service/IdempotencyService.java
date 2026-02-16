package org.ecommerce.project.idempotency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.ecommerce.project.idempotency.model.IdempotencyRecordOrder;
import org.ecommerce.project.idempotency.repository.IdempotencyRepository;
import org.ecommerce.project.payload.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    @Autowired
    private IdempotencyRepository idempotencyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // Note Object mapper converts object into JSON, while modelMapper converts the objects into different format objects

    public ResponseEntity<?> execute(String key, Supplier<ResponseEntity<?>> action){
        Optional<IdempotencyRecordOrder> existing = idempotencyRepository.findByIdempotencyKey(key);
        if (existing.isPresent()){
            IdempotencyRecordOrder record = existing.get();

           try{
               OrderDTO dto = objectMapper.readValue(record.getResponseBody(), OrderDTO.class);

               return ResponseEntity.status(record.getStatusCode()).body(dto);
           } catch (Exception e) {
               return ResponseEntity.status(500).body("Failed to replay response");
           }
        }

        ResponseEntity<?> response = action.get();
        try{
            IdempotencyRecordOrder record = new IdempotencyRecordOrder();
            record.setIdempotencyKey(key);
            record.setResponseBody(objectMapper.writeValueAsString(response.getBody()));
            record.setStatusCode(response.getStatusCodeValue());
            idempotencyRepository.save(record);
        }
        catch (Exception ignored) {}


        return response;
    }


}
