package com.alabs.core_application.utils.mock

import com.apollographql.apollo.api.*
import com.apollographql.apollo.api.Operation.Companion.EMPTY_VARIABLES
import com.apollographql.apollo.api.internal.*
import com.apollographql.apollo.api.internal.QueryDocumentMinifier.minify
import okio.BufferedSource
import okio.ByteString


class MockApollo(private val exeption: Exception) :
    Query<Operation.Data, String, Operation.Variables> {


    val QUERY_DOCUMENT = minify(
        "query{\n" +
                "    hero {\n" +
                "    name\n" +
                "  }\n" +
                "}"
    )

    override fun composeRequestBody(
        autoPersistQueries: Boolean,
        withQueryDocument: Boolean,
        scalarTypeAdapters: ScalarTypeAdapters
    ) =
        OperationRequestBodyComposer.compose(
            this,
            autoPersistQueries,
            withQueryDocument,
            scalarTypeAdapters
        )


    override fun composeRequestBody() =
        OperationRequestBodyComposer.compose(this, false, true, ScalarTypeAdapters.DEFAULT)


    override fun composeRequestBody(scalarTypeAdapters: ScalarTypeAdapters) =
        OperationRequestBodyComposer.compose(this, false, true, scalarTypeAdapters)


    var operationName: OperationName = object : OperationName {
        override fun name(): String {
            return "emptyQuery"
        }
    }

    override fun name() = operationName


    override fun operationId() = ""

    override fun parse(source: BufferedSource): Response<String> {
        throw exeption
    }

    override fun parse(
        source: BufferedSource,
        scalarTypeAdapters: ScalarTypeAdapters
    ): Response<String> {
        throw java.lang.UnsupportedOperationException()
    }

    override fun parse(byteString: ByteString): Response<String> {
        throw exeption
    }

    override fun parse(
        byteString: ByteString,
        scalarTypeAdapters: ScalarTypeAdapters
    ): Response<String> {
        throw exeption
    }

    override fun queryDocument(): String {
        return QUERY_DOCUMENT
    }

    override fun responseFieldMapper(): ResponseFieldMapper<Operation.Data> {
        return object : ResponseFieldMapper<Operation.Data> {
            override fun map(responseReader: ResponseReader): Operation.Data {
                return MockApolloOperationData()
            }
        }
    }

    override fun variables(): Operation.Variables {
        return EMPTY_VARIABLES
    }

    override fun wrapData(data: Operation.Data?): String? {
        return "data"
    }
}

class MockApolloOperationData : Operation.Data {
    override fun marshaller(): ResponseFieldMarshaller {
        return MockApolloField()
    }

}

class MockApolloField : ResponseFieldMarshaller {
    override fun marshal(writer: ResponseWriter) {

    }

}