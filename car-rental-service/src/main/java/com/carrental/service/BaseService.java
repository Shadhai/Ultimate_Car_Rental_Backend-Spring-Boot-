package com.carrental.service;

import com.carrental.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> {
    
    protected abstract JpaRepository<T, ID> getRepository();
    
    public T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " not found with id: " + id));
    }
    
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    public T save(T entity) {
        return getRepository().save(entity);
    }
    
    public void delete(ID id) {
        if (!getRepository().existsById(id)) {
            throw new ResourceNotFoundException(getEntityName() + " not found with id: " + id);
        }
        getRepository().deleteById(id);
    }
    
    public boolean exists(ID id) {
        return getRepository().existsById(id);
    }
    
    protected abstract String getEntityName();
}