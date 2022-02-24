package org.kablambda.cardcreator;

import java.util.List;

public record Card(String question, List<String> answers, int correctIndex) {

}

