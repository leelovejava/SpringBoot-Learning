package com.leelovejava.essearch.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author leelovejava
 * @date 2019/11/2 22:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private Integer totalPage;
    private List<HouseData> list;
}
