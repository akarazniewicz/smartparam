/*
 * Copyright 2014 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.editor.core;

import java.util.Arrays;
import java.util.List;
import org.smartparam.editor.config.ParamEditorConfig;
import org.smartparam.editor.config.ParamEditorConfigBuilder;
import org.smartparam.editor.config.ParamEditorFactory;
import org.smartparam.engine.core.repository.RepositoryName;
import org.smartparam.engine.core.parameter.level.LevelKey;
import org.smartparam.engine.core.parameter.entry.ParameterEntryKey;
import org.smartparam.editor.core.entry.ParameterEntryMapConverter;
import org.smartparam.editor.core.filters.SortDirection;
import org.smartparam.editor.model.simple.SimpleLevel;
import org.smartparam.editor.model.simple.SimpleLevelKey;
import org.smartparam.editor.model.simple.SimpleParameter;
import org.smartparam.editor.model.simple.SimpleParameterEntryKey;
import org.smartparam.editor.core.store.FakeEditableParamRepository;
import org.smartparam.editor.model.simple.SimpleParameterEntry;
import org.smartparam.engine.config.ParamEngineConfig;
import org.smartparam.engine.config.ParamEngineConfigBuilder;
import org.smartparam.engine.config.ParamEngineFactory;
import org.smartparam.engine.core.ParamEngine;
import org.smartparam.engine.core.output.entry.MapEntry;
import org.smartparam.engine.core.parameter.level.Level;
import org.smartparam.engine.core.parameter.ParamRepository;
import org.smartparam.engine.core.parameter.Parameter;
import org.smartparam.engine.core.parameter.entry.ParameterEntry;
import org.smartparam.engine.core.prepared.PreparedParamCache;
import org.smartparam.engine.types.string.StringType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.smartparam.engine.core.parameter.ParameterTestBuilder.parameter;
import static org.smartparam.engine.core.parameter.level.LevelTestBuilder.level;
import static org.smartparam.engine.test.ParamEngineAssertions.assertThat;

/**
 *
 * @author Adam Dubiel
 */
public class BasicParamEditorTest {

    private static final RepositoryName REPOSITORY_NAME = RepositoryName.from("repository");

    private EditableParamRepository editableRepository;

    private PreparedParamCache cache;

    private ParamEditor paramEditor;

    @BeforeMethod
    public void setUp() {
        cache = mock(PreparedParamCache.class);
        editableRepository = mock(EditableParamRepository.class);
        when(editableRepository.getParameterMetadata(anyString())).thenReturn(new SimpleParameter());

        ParamEngineConfig config = ParamEngineConfigBuilder.paramEngineConfig()
                .withParameterRepository("repository", editableRepository)
                .withParameterRepository(mock(ViewableParamRepository.class))
                .withParameterCache(cache)
                .withAnnotationScanDisabled().build();
        ParamEngine paramEngine = ParamEngineFactory.paramEngine(config);

        ParameterEntryMapConverter entryMapConverter = mock(ParameterEntryMapConverter.class);
        when(entryMapConverter.asEntry(any(Parameter.class), any(MapEntry.class))).thenReturn(new SimpleParameterEntry());
        when(entryMapConverter.asMap(any(Parameter.class), any(ParameterEntry.class))).thenReturn(new MapEntry());

        ParamEditorConfig editorConfig = ParamEditorConfigBuilder.paramEditorConfig(paramEngine).build();
        paramEditor = new ParamEditorFactory(editorConfig).editor();
    }

    @Test
    public void shouldRegisterAllEditableRepositoriesFromParamEngineInOrderWhenCreatingNewEditor() {
        // given
        ParamRepository viewableRepository = mock(ViewableParamRepository.class);
        ParamRepository editableRepository1 = new FakeEditableParamRepository();
        ParamRepository editableRepository2 = new FakeEditableParamRepository();

        ParamEngineConfig config = ParamEngineConfigBuilder.paramEngineConfig()
                .withParameterRepository("fake1", editableRepository1)
                .withParameterRepository(viewableRepository)
                .withParameterRepository("fake2", editableRepository2)
                .withAnnotationScanDisabled()
                .withType("string", new StringType())
                .build();
        ParamEngine paramEngine = ParamEngineFactory.paramEngine(config);

        // when
        BasicParamEditor localParamEditor = new BasicParamEditor(paramEngine, null);

        // then
        assertThat(localParamEditor.repositories()).containsOnly(RepositoryName.from("fake1"), RepositoryName.from("fake2"));
    }

    @Test
    public void shouldCreateNewParameterInGivenRepository() {
        // given
        Parameter parameter = new SimpleParameter();

        // when
        paramEditor.createParameter(REPOSITORY_NAME, parameter);

        // then
        verify(editableRepository).createParameter(parameter);
    }

    @Test
    public void shouldUpdateParameterInRepositoryAndInvalidateCacheForThisParameter() {
        // given
        Parameter parameter = new SimpleParameter();

        // when
        paramEditor.updateParameter(REPOSITORY_NAME, "parameter", parameter);

        // then
        verify(editableRepository).updateParameter("parameter", parameter);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldDeleteParameterInRepositoryAndInvalidateCacheForThisParameter() {
        // when
        paramEditor.deleteParameter(REPOSITORY_NAME, "parameter");

        // then
        verify(editableRepository).deleteParameter("parameter");
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldAddLevelToParameterInRepositoryAndInvalidateCacheForThisParameter() {
        // given
        Level level = new SimpleLevel();

        // when
        paramEditor.addLevel(REPOSITORY_NAME, "parameter", level);

        // then
        verify(editableRepository).addLevel("parameter", level);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldUpdateLevelInParameterAndInvalidateCacheForThisParameter() {
        // given
        LevelKey levelKey = new SimpleLevelKey("key");
        Level level = new SimpleLevel();

        // when
        paramEditor.updateLevel(REPOSITORY_NAME, "parameter", levelKey, level);

        // then
        verify(editableRepository).updateLevel("parameter", levelKey, level);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldReorderLevelsInParameterAndInvalidateCacheForThisParameter() {
        // given
        List<LevelKey> newOrder = Arrays.asList((LevelKey) new SimpleLevelKey("key"));

        // when
        paramEditor.reorderLevels(REPOSITORY_NAME, "parameter", newOrder);

        // then
        verify(editableRepository).reorderLevels("parameter", newOrder);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldRemoveLevelFromParameterAndInvalidateCacheForThisParameter() {
        // given
        LevelKey levelKey = new SimpleLevelKey("key");

        // when
        paramEditor.deleteLevel(REPOSITORY_NAME, "parameter", levelKey);

        // then
        verify(editableRepository).deleteLevel("parameter", levelKey);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldAddEntryToParameterAndInvalidateCacheForThisParameter() {
        // given
        MapEntry entry = new MapEntry();

        // when
        paramEditor.addEntry(REPOSITORY_NAME, "parameter", entry);

        // then
        verify(editableRepository).addEntry(eq("parameter"), any(ParameterEntry.class));
        verify(cache).invalidate("parameter");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddMultipleEntriesToParameterInOneTransactionAndInvalidateCacheForThisParameter() {
        // given
        List<MapEntry> entries = Arrays.asList(new MapEntry());

        // when
        paramEditor.addEntries(REPOSITORY_NAME, "parameter", entries);

        // then
        verify(editableRepository).addEntries(eq("parameter"), anyCollection());
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldUpdateEntryInParameterAndInvalidateCacheForThisParameter() {
        // given
        ParameterEntryKey entryKey = new SimpleParameterEntryKey("key");
        MapEntry entry = new MapEntry();

        // when
        paramEditor.updateEntry(REPOSITORY_NAME, "parameter", entryKey, entry);

        // then
        verify(editableRepository).updateEntry(eq("parameter"), eq(entryKey), any(ParameterEntry.class));
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldDeleteEntryFromParameterAndInvalidateCacheForThisParameter() {
        // given
        ParameterEntryKey entryKey = new SimpleParameterEntryKey("key");

        // when
        paramEditor.deleteEntry(REPOSITORY_NAME, "parameter", entryKey);

        // then
        verify(editableRepository).deleteEntry("parameter", entryKey);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldDeleteGivenEntriesFromParameterInSingleTransactionAndInvalidateCacheForThisParameter() {
        // given
        List<ParameterEntryKey> entryKeys = Arrays.asList((ParameterEntryKey) new SimpleParameterEntryKey("key"));

        // when
        paramEditor.deleteEntries(REPOSITORY_NAME, "parameter", entryKeys);

        // then
        verify(editableRepository).deleteEntries("parameter", entryKeys);
        verify(cache).invalidate("parameter");
    }

    @Test
    public void shouldNormalizeGivenMapEntry() {
        // given
        Parameter parameter = parameter()
                .withLevels(level().withName("level").withType("string").build())
                .build();

        MapEntry denormalized = new MapEntry();
        denormalized.put("level", SortDirection.ASC);

        // when
        MapEntry normalized = paramEditor.normalize(parameter, denormalized);

        // then
        assertThat((String) normalized.get("level")).isEqualTo("ASC");
    }
}
