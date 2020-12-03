package com.example.demo.service;

import com.example.demo.model.RespRekv;
import com.example.demo.model.RespType;
import com.example.demo.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class CreateTableService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CreateTableService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Response<RespType> getRespType() {
        Response<RespType> respTypeResponse = new Response<>();
        respTypeResponse.setSuccess(true);

        RespType respType = new RespType();
        respType.setId(2L);
        respType.setCode("FL");
        respType.setName("Физические лица");
        respType.setUrl("http://172.16.0.182:9991/srn-rest/findSurveyObjectByIdentityObj");
        respType.setBaseUrl("http://172.16.0.182:9991/srn-rest");

        respTypeResponse.setObj(respType);
        respTypeResponse.setDescription(null);

        return respTypeResponse;
    }

    public Response<RespRekv> getRespRekv() {

        Response<RespRekv> respRekvResponse = new Response<>();
        respRekvResponse.setSuccess(true);

        List<RespRekv> respRekvList = new ArrayList<>();

        RespRekv respRekv = new RespRekv();
        respRekv.setRekvId(97L);
        respRekv.setNameField("IIN");
        respRekv.setNameKk("ЖСН");
        respRekv.setNameRu("ИИН");
        respRekv.setNameEn("IIN");
        respRekv.setRekvType("DICT");
        respRekv.setAcronymId(null);
        respRekv.setKeyRekv(true);

        RespRekv respRekv2 = new RespRekv();
        respRekv2.setRekvId(109L);
        respRekv2.setNameField("KATO_ID");
        respRekv2.setNameKk("ӘАОЖ(каталог бойынша)");
        respRekv2.setNameRu("КАТО(по каталогу)");
        respRekv2.setNameEn("IIN");
        respRekv2.setRekvType("ACRONYM");
        respRekv2.setAcronymId(67L);
        respRekv2.setKeyRekv(false);

        respRekvList.add(respRekv);
        respRekvList.add(respRekv2);


        respRekvResponse.setList(respRekvList);
        respRekvResponse.setTotalCount(respRekvList.size());
        respRekvResponse.setDescription(null);

        return respRekvResponse;
    }


    public String createSequence(String typeUnitAcc) {
        return "CREATE SEQUENCE BA_CAT_"
                + typeUnitAcc
                + "_SEQ "
                + "START WITH 1 "
                + "INCREMENT BY 1 "
                + "NOCACHE "
                + "NOCYCLE;";
    }

    public String createTable(List<RespRekv> fields, String tableName) {

        if (isExistTable(tableName)) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder
                    .append("CREATE TABLE CUBE.")
                    .append(tableName)
                    .append(" (ID NUMBER(20) PRIMARY KEY, ");

            Iterator<RespRekv> iterator = fields.iterator();
            while (iterator.hasNext()) {
                String nameField = iterator.next().getNameField();
                sqlBuilder.append(nameField);
                if (!iterator.hasNext()) sqlBuilder.append(" VARCHAR(20));");
                else sqlBuilder.append(" VARCHAR(20), ");
            }

            return sqlBuilder.toString().toUpperCase();
        } else throw new RuntimeException("table exists");
    }

    public List<String> createIndexes(List<RespRekv> respRekvList, String tableName, String typeUnitAcc) {
        List<String> indexes = new ArrayList<>();
        for (RespRekv rekv : respRekvList) {
            String nameField = rekv.getNameField();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder
                    .append("CREATE INDEX IDX_BA_CAT_")
                    .append(typeUnitAcc)
                    .append("_")
                    .append(nameField)
                    .append(" ON ")
                    .append(tableName)
                    .append("(")
                    .append(nameField)
                    .append(");");

            indexes.add(sqlBuilder.toString().toUpperCase());
        }

        return indexes;
    }

    public boolean isExistTable(String tableName) {
        String sql = "SELECT count (*) FROM all_tables where TABLE_NAME = :tableName";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("tableName", tableName.toUpperCase());
//        return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class) > 0;
        return true;
    }

    public void test() {
        Response<RespType> respType = getRespType();
        String typeUnitAcc = respType.getObj().getCode(); //typeUnitAcc
        String tableName = "BA_CAT_" + typeUnitAcc;

        Response<RespRekv> respRekv = getRespRekv();
        List<RespRekv> respRekvList = respRekv.getList();

        String sequence = createSequence(typeUnitAcc);

        String table = createTable(respRekvList, tableName);
        StringBuilder indexes = new StringBuilder();
        for (String fl : createIndexes(respRekvList, tableName, typeUnitAcc)) {
            indexes.append(fl);
        }
        log.info("{} {} {}", sequence, table, indexes.toString());
    }
}
