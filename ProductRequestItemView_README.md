
# ProductRequestItemView - Visualização de Itens de Requisição de Produto

## Descrição

O `ProductRequestItemView` é uma classe que gerencia a visualização de itens de uma requisição de produto. Ela permite que o usuário visualize detalhes de uma requisição de produto, incluindo informações sobre o produto ou acessório, quantidade solicitada, quantidade aprovada, quantidade disponível, entre outros dados importantes. Além disso, a classe trata a permissão de acesso com base no perfil do usuário e no status da requisição.

### Funcionalidades

- Exibe informações detalhadas sobre a requisição de produto.
- Permite visualizar a imagem do produto, se disponível.
- Exibe as quantidades solicitada, aprovada, cancelada, disponível e retirada.
- Permite editar ou remover itens da requisição se o status da requisição for "Rascunho".
- Adiciona abas para exibição de detalhes de retiradas e outros dados relacionados ao item da requisição.

### Requisitos

- Java 8 ou superior.
- Framework de web `br.com.firsti`.

### Estrutura do Código

O código é implementado na classe `ProductRequestItemView`, que herda de `AbstractActionView<ModuleProductRequestItem>`. Ele utiliza o construtor `super(new Builder<>(Access.COMPANY_PUBLIC))` para definir a visibilidade e as permissões de acesso. A lógica de exibição dos dados é feita através do método `onWindowRequest`, onde são coletadas as informações do banco de dados e exibidas na interface.

### Como Usar

1. A classe é acionada a partir de uma requisição de ação de visualização de item de requisição de produto.
2. O sistema verifica se o usuário tem permissão para acessar as informações da requisição.
3. A interface exibe os dados, incluindo campos como status da requisição, tipo de produto, quantidade solicitada, aprovada, etc.
4. O usuário pode editar ou remover os itens, se o status da requisição permitir.
