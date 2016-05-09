package cn.edu.njnu.domain;

import java.util.List;

public interface Extractable {

    List<String> targetSamples();

    void generateObject(List<String> var1);

    Extractable getInstance();
}
