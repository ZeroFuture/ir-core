package org.zeroqu.ircore.tokenizer;

import java.io.Serializable;
import java.util.List;

public interface Tokenizer extends Serializable {
    List<String> tokenize(String text);
}
