package com.tencent.strinker;///*
// * Copyright (c) 2017 Yrom Wang
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.tencent.strinker;
//
//import com.android.build.api.transform.DirectoryInput;
//import com.android.build.api.transform.TransformInput;
//import com.google.common.collect.HashBasedTable;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Table;
//
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.FieldVisitor;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//
//import java.io.IOException;
//import java.io.UncheckedIOException;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.PathMatcher;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
//import static org.objectweb.asm.ClassReader.SKIP_FRAMES;
//
///**
// * @author yrom
// */
//public class RSymbols {
//    /**
//     * default package!
//     */
//    static final String R_STYLEABLES_CLASS_NAME = "R$styleable";
//
//    private Map<String, Integer> symbols = Collections.emptyMap();
//    private Map<String, int[]> styleables = Maps.newHashMap();
//
//    // <owner class, field name, field value>
//    private Table<String, String, int[]> styleableTable = HashBasedTable.create();
//
//    private List<String> whiteList;
//    private List<String> whiteListRegex;
//
//    public RSymbols() {
//    }
//
//    public RSymbols(List<String> whiteList) {
//        this.whiteList = transformWhiteList(whiteList);
////        this.whiteListRegex = toRegex(whiteList);
//    }
//
//    /**
//     * @param name
//     * @param isClassName true means the first param is a class name like com.example.R$id.
//     *                    otherwise it is a field with class name like "com.example.R$id.some_id"
//     *                    fixme: bug when name is R$style or R$styleable
//     *                    fixme: using regex pattern
//     * @return
//     */
//    @Deprecated
//    public boolean isInWhiteList(String name, boolean isClassName) {
//        // using slash here for convenience to match the white list.
//        // com/example/R$id.some_id -> com/example/R/id/some_id
//        // com/example/R$id -> com/example/R/id
//        String key = name.replace('.', '/').replace('$', '/');
//        return whiteList != null
//                &&
//                (whiteList.stream()
//                        .filter(s -> s.endsWith("*"))
//                        .map(s -> s.substring(0, s.indexOf("*")))
//                        .anyMatch(s -> isClassName ? s.contains(key) : key.contains(s))
//                        ||
//                        whiteList.stream()
//                                .filter(s -> !s.endsWith("*"))
//                                .anyMatch(s -> isClassName ? s.contains(key) : key.equals(s))
//                );
//    }
//
////    private List<String> toRegex(List<String> origin) {
////        return origin.stream()
////                .peek(s -> {
////                    if (!s.matches(".*R\\.[a-zA-Z*]+\\..+")) {
////                        throw new RuntimeException(String.format("\"%s\" : Illegal format of white list! Please Check inlineR white list configuration!", s));
////                    }
////                })
////                .map(s -> {
////                    StringBuilder sb = new StringBuilder(s);
////                    if (!s.contains("$")) {
////                        sb.setCharAt(s.lastIndexOf(".", s.lastIndexOf(".") - 1), '$');
////                    }
////                    return sb.toString().replace(".", "/").replace("*", ".*").replace("$", "\\$");
////                })
////                .peek(s -> ShrinkerPlugin.logger.debug("[WhiteList]regex = {}", s))
////                .collect(Collectors.toList());
////    }
////
////    public boolean classNameMatchesWhiteList(String className) {
////        ShrinkerPlugin.logger.debug("[WhiteList]class name : {}", className);
////
////        return whiteListRegex != null
////                && whiteListRegex.stream().anyMatch(regex -> className.matches(regex.substring(0, regex.lastIndexOf("/"))));
//////                                                                                com/pkg/R$id
////    }
////
////    public boolean fieldNameMatchesWhiteList(String fieldName) {
////        String key = fieldName.replace('.', '/');
////        ShrinkerPlugin.logger.debug("[WhiteList]field name mapped for pattern: {} -> {}", fieldName, key);
////
////        return whiteListRegex != null
////                && whiteListRegex.stream().anyMatch(key::matches);
//////                                                  com/pkg/R$id/action
////    }
////
////    public boolean pathMatchesWhiteList2(String path) {
////        ShrinkerPlugin.logger.debug("[WhiteList]path : {}", path);
////
////        return whiteListRegex != null
////                && whiteListRegex.stream().anyMatch(regex -> path.matches(".*" + regex.substring(0, regex.lastIndexOf('/')) + "\\.class$"));
//////                                                                                .*com/pkg/R$id\.class$
////    }
//
//    @Deprecated
//    public boolean pathMatchesWhiteList(String path) {
//        String key = path.replace('.', '/').replace('$', '/');
//        return whiteList != null &&
//                whiteList.stream()
//                        .map(s -> s.endsWith("*")
//                                ? s.substring(0, s.indexOf("*"))
//                                : s.substring(0, s.lastIndexOf('/')))
//                        .anyMatch(key::contains);
//    }
//
//    public Integer get(String key) {
//        return symbols.get(key);
//    }
//
//    public boolean containsKey(String key) {
//        return symbols.containsKey(key);
//    }
//
//    public boolean isEmpty() {
//        return symbols.isEmpty() && styleables.isEmpty();
//    }
//
//    public Map<String, int[]> getStyleables() {
//        return Collections.unmodifiableMap(styleables);
//    }
//
//    public RSymbols from(Collection<TransformInput> inputs) {
//
//        final List<Path> paths = inputs.stream()
//                .map(TransformInput::getDirectoryInputs)
//                .flatMap(Collection::stream)
//                .map(this::toStream)
//                .reduce(Stream.empty(), Stream::concat)
//                .collect(Collectors.toList());
//        Stream<Path> stream;
//        if (paths.size() >= Runtime.getRuntime().availableProcessors() * 3) {
//            // use parallel here!
//            stream = paths.stream();
//            symbols = Maps.newConcurrentMap();
//        } else {
//            stream = paths.stream();
//            symbols = Maps.newHashMap();
//        }
//
//        final PathMatcher rClassMatcher = FileSystems.getDefault().getPathMatcher("glob:R$*.class");
//        stream.filter(path -> rClassMatcher.matches(path.getFileName()))
//                .forEach(this::drainSymbols);
//        return this;
//    }
//
//    private void drainSymbols(Path file) {
////        final String filename = file.getFileName().toString();
////        String typeName = filename.substring(0, filename.length() - ".class".length());
//        byte[] bytes;
//        try {
//            bytes = Files.readAllBytes(file);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//
//        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5) {
//
//            String className;
//
//            @Override
//            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//                super.visit(version, access, name, signature, superName, interfaces);
//                className = name;
//            }
//
//            @Override
//            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//                // read constant value
//                if (value instanceof Integer) {
//                    String key = className + '.' + name;
//                    Integer old = symbols.get(key);
//                    if (old != null && !old.equals(value)) {
//                        throw new IllegalStateException("Value of " + key + " mismatched! "
//                                + "Excepted 0x" + Integer.toHexString(old)
//                                + " but was 0x" + Integer.toHexString((Integer) value));
//                    } else {
////                        if (fieldNameMatchesWhiteList(key)) {
////                            return null;
////                        }
//                        symbols.put(key, (Integer) value);
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                if (access == Opcodes.ACC_STATIC && "<clinit>".equals(name)) {
//
//                    return new MethodVisitor(Opcodes.ASM5) {
//                        int[] current = null;
//                        boolean inError;
//                        LinkedList<Integer> intStack = new LinkedList<>();
//
//                        @Override
//                        public void visitIntInsn(int opcode, int operand) {
//                            if (opcode == Opcodes.NEWARRAY && operand == Opcodes.T_INT) {
//                                if (intStack.size() > 0) {
//                                    current = new int[intStack.pop()];
//                                } else {
//                                    inError = true;
//                                    System.err.println(String.format("%s : %s -> %s", className, "visitIntInsn", "NEWARRAY inError"));
//                                }
//                            } else if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
//                                intStack.push(operand);
//                            }
//                        }
//
//                        @Override
//                        public void visitIincInsn(int var, int increment) {
//                            super.visitIincInsn(var, increment);
//                        }
//
//                        @Override
//                        public void visitVarInsn(int opcode, int var) {
//                            super.visitVarInsn(opcode, var);
//                        }
//
//                        @Override
//                        public void visitLdcInsn(Object cst) {
//                            if (cst instanceof Integer) {
//                                intStack.push((Integer) cst);
//                            }
//                        }
//
//                        @Override
//                        public void visitInsn(int opcode) {
//                            if (opcode >= Opcodes.ICONST_0 && opcode <= Opcodes.ICONST_5) {
//                                intStack.push(opcode - Opcodes.ICONST_0);
//                            } else if (opcode == Opcodes.IASTORE) {
//                                if (!inError) {
//                                    int value = intStack.pop();
//                                    int index = intStack.pop();
//                                    if (current != null) {
//                                        current[index] = value;
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//                            if (opcode == Opcodes.PUTSTATIC) {
//                                if (!inError) {
//                                    int[] old = styleables.get(name);
//                                    if (old != null && old.length != current.length && !Arrays.equals(old, current)) {
//                                        throw new IllegalStateException("Value of styleable." + name + " mismatched! "
//                                                + "Excepted " + Arrays.toString(old)
//                                                + " but was " + Arrays.toString(current));
//                                    } else {
//                                        styleables.put(name, current);
//                                    }
//                                }
//                                inError = false;
//                                current = null;
//                                intStack.clear();
//                            }
//                        }
//                    };
//                }
//                return null;
//            }
//        };
//
//        new ClassReader(bytes).accept(visitor, SKIP_DEBUG | SKIP_FRAMES);
//    }
//
//    private List<String> transformWhiteList(List<String> origin) {
//        return origin.stream()
//                .map(s -> s.replace('.', '/'))
//                .collect(Collectors.toList());
//    }
//
////    private List<String> toRegex(List<String> origin) {
////        return origin.stream().map(s -> s.replace(".","\\.").replace("$","\\$"))
////                .collect(Collectors.toList());
////    }
//
//    private Stream<Path> toStream(DirectoryInput dir) {
//        try {
//            return Files.walk(dir.getFile().toPath()).filter(Files::isRegularFile);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//}
