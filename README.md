# CDSS Test Engine

## Overview

This service implements a Clinical Decision Support System.

The Clinical Decision Support System is responsible for making clinical decisions, and communicating these to the EMS Test Harness

This proof of concept implementation is compliant with both v1.1 and v2.0 of the CDS API Spec and supports:

- Responding to searches for a service definition
- Responding to the triage $evaluate interaction.
- Generating Care Plan and Referral Request resources as a result of triage.
- Implementations of several clinical scenarios

## Source Code Location

The repo for this project is located in a public GitLab space here: https://gitlab.com/ems-test-harness/cds-test-engine

## Usage

### Prerequisites
Make sure you have everything installed as per the setup guide:
- Maven
- IntelliJ IDE (Recommended)

### Build Steps
To run the CDS Test Engine, simply run the maven task:

`mvn spring-boot:run`

By default, logs are formatted with the full JSON context, but you can optionally add a spring profile to the maven task for cleaner logging:

- `-Dspring.profiles.active=dev` will output a simple 'TIME THREAD LEVEL MESSAGE' format
- `-Dspring.profiles.active=prettylogs` will output the JSON logs in an easier to read format.

## Project Structure
### Implementation
The CDSS is a Java Spring Application. It is split into three major layers:

1. Resource Providers - These contain FHIR end points for various resources that the CDSS serves (such as Questionnaire's and Service Definitions).
2. Transformation Layer - This contains transformations from the HAPI Library's FHIR Model to our own domain model and vice versa.
3. Engine Layer - This layer contains the business logic of the service definitions using the Drools engine. The rules files are located in the resources/drools package.
There are also packages for:

- Utilities
- Configuration (For Spring, security and fhir server)
- Logging
- 
Static resources are provided in `resources/images`, `resources/questionnaires` and `resources/servicedefinitions`.


### Tests
Unit tests are provided for the majority of the transformation layer and for various 'routes' through the logic in the engine layer. There are also unit tests for the more complex methods in the resource provider layer.

These are all in the `src/test/java...` package.

A manual test pack for various CDS scenarios is located here and this can driven from the the EMS UI.

## Licence

Unless stated otherwise, the codebase is released under [the MIT License][mit].
This covers both the codebase and any sample code in the documentation.

The documentation is [© Crown copyright][copyright] and available under the terms
of the [Open Government 3.0][ogl] licence.

[mit]: LICENSE
[copyright]: http://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/
[ogl]: http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
