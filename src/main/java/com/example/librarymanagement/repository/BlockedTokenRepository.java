package com.example.librarymanagement.repository;


import com.example.librarymanagement.model.BlockedTokenModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepository extends CrudRepository<BlockedTokenModel, String> {

}
