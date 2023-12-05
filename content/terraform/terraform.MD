---
layout: page
title: Terraform
permalink: /terraform/
comments: true
---

# Table of contents

- [Table of contents](#table-of-contents)
  - [Syntax](#syntax)
  - [Variables](#variables)
    - [Best practices](#best-practices)
  - [Values](#values)
  - [Secrets](#secrets)
  - [Environmental variables](#environmental-variables)
  - [Locals](#locals)
  - [Outputs](#outputs)
  - [Data sources](#data-sources)
  - [Dynamic expressions](#dynamic-expressions)
    - [splat](#splat)
    - [count](#count)
  - [Modules](#modules)
  - [State](#state)
    - [Local state vs remote state](#local-state-vs-remote-state)
    - [Remote state](#remote-state)
  - [Others](#others)


## Syntax
```
<BLOCK TYPE> "<BLOCK LABEL>" "<BLOCK_LABEL"> {
   # Block body
   <IDENTIFIER> = <EXPRESSION> # Argument
}
```


## Variables

1. Terraform auto-converts variables into target type.
    - when anticipating an int (`2`), you can provide string (`"2"`) and it's gonna work
2. You can use `terraform console` to evaluate expressions
    - Console loads configuration on startup, so remember to restart it to refresh config.
    - Type `exit` to quit
3. *Collection* variable types can contain more than one value of the same type:
   - List
   - Map
   - Set
4. *Structural* types have fixed number of values which can be of different types
   - Tuple - sequence of values
   - Object - lookup table
5. Variates can be specified inline by `-var` parameter. It can be both simple or more complex values.
    - `terraform apply -var ec2_instance_type=t2.micro`
    - `terraform apply -var='resource_tags={project="project-name",environment="env-name"}'`
6. Variables can be validated in `validation` block


### Best practices

1. Only parametrize values that will be different e.g. for each instance or environment
2. Changing a variable with a default value is backward-compatible
3. Removing a variable is not backward-compatible
4. For root modules, provide variables in `*.tfvars` file
5. It's good to include the unit in the name, e.g. `ram_size_gb`


## Values

1. Terraform automatically loads files from the current directory
   - `terraform.tfvars` 
   - or matching `*.auto.tfvars`
2. You can also specify file using `-var-file`
    - `terraform apply -var-file="secrets.tfvars"`


## Secrets

1. Flagging variables as sensitive make Terraform redact them from output while running `plan`, `apply` or `destroy`.
2. Sensitive vs sensitive values
   - When marking variable as sensitive, it will be shown in logs like \
   `~ password = (sensitive)`
   -  However some values may be considered as sensitive by provider, they will be printed as\
   `~ password = (sensitive value)`
   - Nonetheless you should always mark sensitive values appropriately to make sure they won't be disclosed e.g. when referencing them in other locations.
   - It is advised to keep such variables in a separate file and exclude it from version control.
3. Terraform will raise an error when detects some sensitive may be exposed.


## Environmental variables

1. Terraform automatically reads `TF_VAR_<VARIABLE_NAME>` environmental variables
   - Tip: when using envs for sensitive values, keep in mind they may be visible in command line history


## Locals

1. In contradiction to input variables, locals are not set by users of the configuration
2. In contradiction to variable values, locals can use dynamic expressions and resource arguments
3. It is common to have all resources tagged in a some way to track them.
   - You can enforce this by using locals in combination with variables such as project name or environment.
4. Reusing locals may simplify configuration, but it can add complexity to resource lifecycle
5. Locals can help avoid repeating the same values or expressions, but it make it harder to read configurations as actual values won't be clearly visible
6. It's good to use locals in moderation, when a single value or result is used in many places and it is likely to change in the future.
7. Locals main use case is to easily change a value in a central place.


## Outputs

1. Outputs can export structured data about resources, which can also be used e.g. to configure other parts of infrastructure with automation tools.
   - They can also act as a data source for another Terraform workspaces.
   - They can also expose data from a child module to a root module.
2. Outputs are stored in the state file.
3. Outputs require refresh by applying the new configuration (even when nothing else changes).
4. Use `terraform output` to show all 
   - Use `terraform output [element]` to show only one.
5. String outputs are wrapped in quotes by default. To disable it, use `-raw` flag when querying
   - `terraform output -raw prod_url`
6. You can use `-json` flag to make outputs JSONs.
   1. Warning! Sensitives won't be redacted in this case!
7. In general, Terraform **will** redact sensitive outputs when invoking `plan`, `apply`, `destroy` or `terraform output`
   - Terraform **will not** redact in any other case, like:
     - querying single output by name
     - querying all outputs into a JSON
     - using outputs from a child module in the root module


## Data sources

1. Data sources allow to fetch data from APIs or other Terraform state backends. Examples are machine image IDs from cloud provider or Terraform outputs from other configurations.


## Dynamic expressions

### splat

1. Splat expression captures all objects sharing given attribute.
2. Without splat expressions, Terraform would return the first item in the array, instead of iterating.
3. Example
    ```tf
    output "private addresses" {
        description = "Private DNSs for AWS instances"
        value       = aws_instance.ubuntu[*].private_dns
    }
    ```

### count

1. Count replicates resource/module a given number of times with an incrementing counter
2. The name of resource/module like `aws_instance.app` refers to all instances then. To reference the individual one, use indexing notation like `aws_instance.app[0]`
3. You can create a list of all values with a `*`
   - `aws_instance.app.*.id` will be a list of all IDs of instances
   - Example:
     ```tf
      resource "aws_instance" "app" {
         # ...
         count           = var.instances_per_subnet * length(module.vpc.private_subnets)
         subnet_id       = module.vpc.private_subnets[count.index % length(module.vpc.private_subnets)]
         # ...
         }

         module "elb_http" {
         # ...
         number_of_instances     = length(aws_instance.app)
         instances               = aws_instance.app.*.id
         # ...
      }
     ```


## Modules

1. A module is a collection of configuration files
2. Modules let you group a set of resources and reuse them later
3. The root module is the one where other modules and resources are instantiated
4. Define the reusable code within a module, so that any change you make to the module will be reflected across all the environments that you plan to reuse


## State

### Local state vs remote state

1. No shared access
   - You have to synchronize manually in order to be up-to-date
2. No locking
   - When multiple team members run Terraform at the same time, nothing prevents them from overriding each other work
3. No confidentiality
   - State file exposes sensitive data

### Remote state

1. Shared access
   - Data is always present for everyone 
2. Locking
   - Multiple team members can work on configuration without issues 
3. Confidentiality
   - Secrets are not stored in a local file



## Others

1. When running terraform with a local state file, it's stored in `terraform.tfstate` in **plaintext**. It's required to be able to compare if secret values changed.
   - Therefore marking variables as sensitive may be not enough to protect them. You have to secure them also e.g. while passing into Terraform configuration. You may use e.g. Vault for secret management.
2. Dependency graph
   - `terraform graph | dot -Tsvg > graph.svg`
3. Refreshing state
   - `terraform refresh`
