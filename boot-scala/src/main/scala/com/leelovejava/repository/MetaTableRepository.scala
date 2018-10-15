package com.leelovejava.repository

import com.leelovejava.domain.MetaTable
import org.springframework.data.repository.CrudRepository

trait MetaTableRepository extends CrudRepository[MetaTable, Integer]{

}
