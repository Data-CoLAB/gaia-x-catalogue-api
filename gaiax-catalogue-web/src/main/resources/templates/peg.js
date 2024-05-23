{
    class AND {
        constructor(exps) {
        if (Array.isArray(exps)) {
            this.and = exps;
        } else {
            this.and = [exps];
        }
          }
        not() {
            return new OR(this.and.map(function (x) { return x.not();}));
        }

    }

    class OR {
        constructor(exps) {
        if (Array.isArray(exps)) {
                   this.or = exps;
               } else {
                   this.or = [exps];
               }
               }
        not() {
            return new AND(this.or.map(function (x) { return x.not();}));
        }

    }
    class Relation {
        constructor(node, operator, edge,value,key) {
            this.node = node;
            this.operator = operator;
            this.edge = edge;
            this.value = value; // remove simple quote
            this.key=key;
        }
        not() {
            this.neg = ! this.neg;
            return this;
        }
    }

    class Attribute {
          constructor(node, operator,value,key,edge) {
                    this.node = node;
                    this.key = key;
                    this.operator = operator;
                    this.value = value; // remove simple quote
                    this.neg = false;
                    if(edge!=""){
                      this.edge = edge;
                   }
                }
        not() {
        this.neg = ! this.neg;
                  return this;
        }
    }
}

start = logical_or: logical_or {

    return logical_or;
}


logical_or = left:logical_and _ "OR" _ right:logical_or {
        return new OR([left, right]);
} / logical_and

logical_and = left:expression _ "AND" _ right:logical_and {
        return new AND([left, right]);
} / expression

expression = "NOT" _ operand:expression {
        return operand.not();
} / primary

primary = lparenthesis _ logical_or:(logical_or) _ rparenthesis {
        return logical_or;
} / assertion

lparenthesis "parenthesis '('" = "("
rparenthesis "parenthesis ')'" = ")"

assertion = rules

_ "whitespace" = [ \t\n\r]* {
    return " ";
}
rules =
<#assign separator = "" >
<#list rules as rule>
  <#if rule.dst??>
  ${separator} node:"${rule.src}" _ operator:"${rule.rel}" _ dest:${rule.dst?lower_case}{
             const destList = dest.split(',').map(value => value.trim());
              return new Relation("${rule.dst}", "IN", operator,destList,"name");
          }  <#assign separator = " / ">

          / node:"${rule.src}" _ operator:"${rule.rel}" _ op:("ALL"/ "ANY" / "IN") "(" _ head:${rule.dst?lower_case} _ tail:("," _ ${rule.dst?lower_case} )* _ ")" {
              let dests = tail.reduce(function(result, element) {
                  result.push(element[2])
                  return result;
              }, [head]);

              if (op=="ALL") {
                 dests = dests.map(function (x) {
                  const xVal= x.split(',').map(value => value.trim())

                 return new Relation("${rule.dst}","IN",operator, xVal,"name");
                 })
                        return new AND(dests);
              } else {
                  return  new Relation("${rule.dst}","IN",operator, dests,"name");
              }
          }

          / node:"${rule.src}" "." key:${rule.src?lower_case}_keys _ operator:("=" / "!="/ "CONTAIN") _ value:value {
                         const destList = value.split(',').map(value => value.trim());
                                let rel="";
                                return new Attribute(node, operator,destList, key,rel);

                 }
                 / node:"${rule.src}" "." key:${rule.src?lower_case}_keys _ operator:( "ANY" / "IN") "(" _ head:value _ tail:("," _ value )* _ ")" {
                                              let dests = tail.reduce(function(result, element) {
                                                        result.push(element[2])
                                                            return result;
                                                          }, [head]);
                                           let rel="";

                                           if (operator=="ALL") {
                                                            dests = dests.map(function (x) {
                                                             const xVal= x.split(',').map(value => value.trim())

                                                            return new Attribute(node, "IN",x, key,rel);
                                                            })
                                                                   return new AND(dests);
                                                         } else{
                                                            return new Attribute(node, "IN",dests, key,rel);
                                                         }
                    }
  <#else>
    / node:"${rule.src}" "." key:dataaccountexport_keys _ operator:("=" / "!="/ "CONTAIN") _ value:value {
                         const destList = value.split(',').map(value => value.trim());
              return new Relation(node, operator,"${rule.rel}" ,destList,key);

                 }
     / node:"${rule.src}" "." key:dataaccountexport_keys _ operator:( "ANY" / "IN") "(" _ head:value _ tail:("," _ value )* _ ")" {
                                              let dests = tail.reduce(function(result, element) {
                                                        result.push(element[2])
                                                            return result;
                                                          }, [head]);
              return new Relation(node, "IN","${rule.rel}" ,dests,key);
                    }
  </#if>



</#list>

<#list srcMap as key, values>
${key?lower_case}_keys=val:( <#list values as value>  "${value}"<#if value_has_next> / </#if></#list> ){
return val;
}
</#list>

<#list destMap as key, values>
${key?lower_case}=val:(<#list values as value>  "${value}"<#if value_has_next> / </#if> </#list>){
    return val;
   }
</#list>

value "value" = "'" val:[^']* "'" {
    return val.join('');
}


