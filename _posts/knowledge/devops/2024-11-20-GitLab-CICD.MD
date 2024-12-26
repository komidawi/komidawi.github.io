---
title: GitLab CI/CD
date: 2024-11-20 00:00:00 +0100
categories: [ devops ]
tags: [ gitlab, cicd ] # TAG names should always be lowercase
---

## Links

1. **[docs.gitlab.com/ee/ci/examples](https://docs.gitlab.com/ee/ci/examples/)**
    - CI/CD Templates
    - Example projects
2. **[docs.gitlab.com/ee/ci/yaml](https://docs.gitlab.com/ee/ci/examples/)**
    - YAML Syntax Reference
3. **[learnxinyminutes.com/docs/yaml](https://learnxinyminutes.com/docs/yaml/)**
    - YAML Cheatsheet

## Knowledge

1. Pipeline
    - Pipeline is what you’re defining in the `.gitlab-ci.yml` file, and is what happens when the contents of the file
      are run on a runner.
    - A pipeline consists of Jobs and Stages
    - Creating a Git tag or adding a Pipeline schedule, do not trigger Pipelines unless you add rules for those cases
2. Stage
    - Defines order of execution
    - Typical stages are `build`, `test`, `deploy`
    - If there are runners available, jobs in a single stage run in parallel
3. Job
    - Job specifies tasks for each stage
    - Example Jobs are `compile` or `unit test` and `integration test`
    - Each Job belongs to a Stage
4. Variables
    1. Scopes
        - `project`
        - `group`
        - `instance`
    2. Types
        1. [Predefined Variables](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html)
            - Set by GitLab, they provide info about current job, pipeline, environment
            - Usually prefixed with `$CI_`
        2. Custom Variables
            - Created and managed in GitLab UI, API, or in configuration files
    3. Visibility modifiers
        1. Protected
            - Only available to jobs running on protected branches or tags
        2. Masked
            - Values are hidden in job logs
5. CI/CD Component
    - Reusable pipeline configuration unit
    - Use a CI/CD Component to compose an entire pipeline configuration or a small part of a larger pipeline
    - You can add a component to your pipeline configuration with `include:component`
6. `script`
    - Each command executes as if it was run as a CLI command.
    - By default, if a command fails or returns an error, the job is flagged as failed and no more commands run.
7. `artifacts`
    - If you want files generated in one job to be used in another job, save them as artifacts.
    - Example config:
        - ```yaml
          artifacts:
            paths:
              - "build/"
          ```
8. `stages`
    - `stages` lists Stages and their order
    - Jobs in the same Stage can run in parallel
    - Jobs in later Stages wait for jobs in earlier Stages to complete
    - If a Job fails, the whole Stage is considered failed and Jobs in later Stages do not start
9. `allow_failure`
    - Jobs which fail won't stop the whole Pipeline
10. `dependencies`
    - Use to fetch Artifacts from other Jobs
    - Set `dependencies: []` to speed up Job by not fetching Artifacts
11. Hidden Jobs
    - Jobs that start with dot `.` are never added to Pipeline
    - Useful for common configurations
12. `extends`
    - Used to apply configuration
    - Mostly used to apply Hidden Jobs
13. `default`
    - Sets keyword defaults when given configuration not defined in Job
    - E.g. in `default: image: ruby:3.0` the `image` will be set to `ruby:3.0` for all Jobs without defined `image`
    - Otherwise `image` will be overridden with `image` specified in Job
14. Some tips
    - You can validate `.gitlab-ci.yml` file on GitLab
        - There's also available JSON Schema
    - Use `needs` to make dependencies
    - Use `rules` to add conditions
    - Use `cache` and `artifacts` to persist files
    - Use `default` to add configuration for all jobs
        - Often used to define `before_script` and `after_script`
    - Use `extends` and `default` to avoid duplication

## YAML Features

1. Anchor `&`
    - Used to duplicate content across document (duplicate or inherit properties)
    - Valid only in files where defined
    - Use Anchors with Hidden Jobs to provide templates for Jobs
    - In case of duplicated keys, the latest included one key wins (overriding previous ones)
    - Example: `.job_template &job_configuration`
2. Merging `<<`
    - Pastes referenced content
    - Example: `<<: *job_configuration`
3. Including Anchor `*`

### Anchor used to build arrays with multiple components

```yaml
.default_scripts: &default_scripts
    - ./default-script1.sh
    - ./default-script2.sh

job1:
    script:
        - *default_scripts
        - ./job-script.sh
```

### Anchor with merging

```yaml
  .job_template: &job_configuration
  image: ruby:2.6
  services:
      - postgres
      - redis

  test1:
      <<: *job_configuration
      script:
          - test1 project

  test2:
      <<: *job_configuration
      script:
          - test2 project
```

### Anchors for scripts

You can use YAML Anchors with `script`, `before_script`, and `after_script` to use predefined commands in multiple jobs

```yaml
.some-script-before: &some-script-before
    - echo "Execute this script first"

.some-script: &some-script
    - echo "Execute this script second"
    - echo "Execute this script too"

.some-script-after: &some-script-after
    - echo "Execute this script last"

job1:
    before_script:
        - *some-script-before
    script:
        - *some-script
        - echo "Execute something, for this job only"
    after_script:
        - *some-script-after

job2:
    script:
        - *some-script-before
        - *some-script
        - echo "Execute something else, for this job only"
        - *some-script-after
```

## Configurations

### Maven config

- Environment variables make Maven use repository homedir instead of the user home for config and dependencies
- `.m2/repository` folder is where all the Maven files are stored
- `target` folder is where application is created

```yaml
variables:
    MAVEN_CLI_OPTS: "-s .m2/settings.xml --batch-mode"
    MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"

cache:
    paths:
        - .m2/repository/
        - target/
```
