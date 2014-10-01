package com.jetdrone.vertx.extras

import com.jetdrone.vertx.yoke.GYoke
import com.jetdrone.vertx.yoke.middleware.*
import org.vertx.groovy.platform.Verticle

public class SwaggerExample extends Verticle {

    @Override
    def start() {

        final GYoke yoke = new GYoke(this);
        final GRouter router = new GRouter();
        final GSwagger swagger = new GSwagger(router, "1.0.0");

        swagger.setInfo([
                title            : "Swagger Sample App",
                description      : "This is a sample server Petstore server. You can find out more about Swagger at <a href=\"http://swagger.wordnik.com\">http://swagger.wordnik.com</a> or on irc.freenode.net, #swagger. For this sample, you can use the api key \"special-key\" to test the authorization filters",
                termsOfServiceUrl: "http://helloreverb.com/terms/",
                contact          : "apiteam@wordnik.com",
                license          : "Apache 2.0",
                licenseUrl       : "http://www.apache.org/licenses/LICENSE-2.0.html"
        ]);

        yoke.use(new BodyParser());
        yoke.use(new ErrorHandler(true));
        yoke.use(new Static("swagger-ui-2.0.12"));
        yoke.use(router);

        swagger.createResource("/pet", "Operations about pets")
                .produces("application/json", "application/xml", "text/plain", "text/html")
                .addModel("Pet", [
                    required: ["id", "name"],
                    properties: [
                        id: [
                            type: "integer",
                            format: "int64",
                            description: "unique identifier for the pet",
                            minimum: "0.0",
                            maximum: "100.0"
                        ],
                        name: [
                            type: "string"
                        ],
                        photoUrls: [
                            type: "array",
                            items: [
                                type: "string"
                            ]
                        ],
                        status: [
                            type: "string",
                            description: "pet status in the store",
                            enum: ["available", "pending", "sold"]
                        ]
                    ]
                ])
                .get("/pet/:petId", "Find pet by ID", [
                        notes           : "Returns a pet based on ID",
                        type            : "Pet",
                        nickname        : "getPetById",
                        authorizations  : [:],
                        parameters      : [
                                [
                                        name         : "petId",
                                        description  : "ID of pet that needs to be fetched",
                                        required     : true,
                                        type         : "integer",
                                        format       : "int64",
                                        paramType    : "path",
                                        allowMultiple: false,
                                        minimum      : "1.0",
                                        maximum      : "100000.0"
                                ]
                        ],
                        responseMessages: [
                                [code: 400, message: "Invalid ID supplied"],
                                [code: 404, message: "Pet not found"]
                        ]
                ]);

        router.get("/pet/:petId") { req ->
            req.response.end()
        }

        yoke.listen(8080);

        container.logger.info("Yoke server listening on port 8080");
    }
}
