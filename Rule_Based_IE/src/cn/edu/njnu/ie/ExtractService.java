package cn.edu.njnu.ie;

import cn.edu.njnu.domain.Extractable;

public interface ExtractService {

    void serialize(String path);

    void unSerialize(String path);

    void generateRule(String var1, Extractable var2);

    Extractable extractResult(String var1);
}
