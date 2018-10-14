package com.leelovejava.service

import com.leelovejava.domain.MetaTable
import com.leelovejava.repository.MetaTableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MetaTableService @Autowired()(metaTableRepository:MetaTableRepository){

  @Transactional
  def save(metaTable:MetaTable) = {
    metaTableRepository.save(metaTable)
  }


  def query() = {
    metaTableRepository.findAll()
  }

}
