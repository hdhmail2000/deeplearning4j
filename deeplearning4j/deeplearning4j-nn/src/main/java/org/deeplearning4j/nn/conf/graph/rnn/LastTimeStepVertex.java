/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  *  See the NOTICE file distributed with this work for additional
 *  *  information regarding copyright ownership.
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package org.deeplearning4j.nn.conf.graph.rnn;

import lombok.Data;
import org.deeplearning4j.nn.conf.graph.GraphVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.inputs.InvalidInputTypeException;
import org.deeplearning4j.nn.conf.memory.LayerMemoryReport;
import org.deeplearning4j.nn.conf.memory.MemoryReport;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.shade.jackson.annotation.JsonProperty;

@Data
public class LastTimeStepVertex extends GraphVertex {

    private String maskArrayInputName;

    /**
     *
     * @param maskArrayInputName The name of the input to look at when determining the last time step. Specifically, the
     *                           mask array of this time series input is used when determining which time step to extract
     *                           and return.
     */
    public LastTimeStepVertex(@JsonProperty("maskArrayInputName") String maskArrayInputName) {
        this.maskArrayInputName = maskArrayInputName;
    }

    @Override
    public GraphVertex clone() {
        return new LastTimeStepVertex(maskArrayInputName);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LastTimeStepVertex)) {
            return false;
        }

        LastTimeStepVertex ltsv = (LastTimeStepVertex) o;
        if (maskArrayInputName == null && ltsv.maskArrayInputName != null
                        || maskArrayInputName != null && ltsv.maskArrayInputName == null)
            return false;
        return maskArrayInputName == null || maskArrayInputName.equals(ltsv.maskArrayInputName);
    }

    @Override
    public int hashCode() {
        return (maskArrayInputName == null ? 452766971 : maskArrayInputName.hashCode());
    }

    @Override
    public long numParams(boolean backprop) {
        return 0;
    }

    @Override
    public int minVertexInputs() {
        return 1;
    }

    @Override
    public int maxVertexInputs() {
        return 1;
    }

    @Override
    public org.deeplearning4j.nn.graph.vertex.impl.rnn.LastTimeStepVertex instantiate(ComputationGraph graph,
                                                                                      String name, int idx, INDArray paramsView, boolean initializeParams, DataType networkDatatype) {
        return new org.deeplearning4j.nn.graph.vertex.impl.rnn.LastTimeStepVertex(graph, name, idx, maskArrayInputName, networkDatatype);
    }

    @Override
    public InputType getOutputType(int layerIndex, InputType... vertexInputs) throws InvalidInputTypeException {
        if (vertexInputs.length != 1)
            throw new InvalidInputTypeException("Invalid input type: cannot get last time step of more than 1 input");
        if (vertexInputs[0].getType() != InputType.Type.RNN) {
            throw new InvalidInputTypeException(
                            "Invalid input type: cannot get subset of non RNN input (got: " + vertexInputs[0] + ")");
        }

        return InputType.feedForward(((InputType.InputTypeRecurrent) vertexInputs[0]).getSize());
    }

    @Override
    public MemoryReport getMemoryReport(InputType... inputTypes) {
        //No additional working memory (beyond activations/epsilons)
        return new LayerMemoryReport.Builder(null, LastTimeStepVertex.class, inputTypes[0],
                        getOutputType(-1, inputTypes)).standardMemory(0, 0).workingMemory(0, 0, 0, 0).cacheMemory(0, 0)
                                        .build();
    }

    @Override
    public String toString() {
        return "LastTimeStepVertex(inputName=" + maskArrayInputName + ")";
    }
}
