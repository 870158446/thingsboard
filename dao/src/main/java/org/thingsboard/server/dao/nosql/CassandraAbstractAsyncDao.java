/**
 * Copyright © 2016-2020 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.nosql;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.common.util.ThingsBoardThreadFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by ashvayka on 21.02.17.
 */
public abstract class CassandraAbstractAsyncDao extends CassandraAbstractDao {

    protected ExecutorService readResultsProcessingExecutor;

    @PostConstruct
    public void startExecutor() {
        readResultsProcessingExecutor = Executors.newCachedThreadPool(ThingsBoardThreadFactory.forName("cassandra-callback"));
    }

    @PreDestroy
    public void stopExecutor() {
        if (readResultsProcessingExecutor != null) {
            readResultsProcessingExecutor.shutdownNow();
        }
    }

    protected <T> ListenableFuture<T> getFuture(TbResultSetFuture future, java.util.function.Function<AsyncResultSet, T> transformer) {
        return Futures.transform(future, new Function<AsyncResultSet, T>() {
            @Nullable
            @Override
            public T apply(@Nullable AsyncResultSet input) {
                return transformer.apply(input);
            }
        }, readResultsProcessingExecutor);
    }

    protected <T> ListenableFuture<T> getFutureAsync(TbResultSetFuture future, com.google.common.util.concurrent.AsyncFunction<AsyncResultSet, T> transformer) {
        return Futures.transformAsync(future, new AsyncFunction<AsyncResultSet, T>() {
            @Nullable
            @Override
            public ListenableFuture<T> apply(@Nullable AsyncResultSet input) {
                try {
                    return transformer.apply(input);
                } catch (Exception e) {
                    return Futures.immediateFailedFuture(e);
                }
            }
        }, readResultsProcessingExecutor);
    }

    protected ListenableFuture<List<Row>> allRows(AsyncResultSet resultSet) {
        return ResultSetUtils.allRows(resultSet, readResultsProcessingExecutor);
    }

}
