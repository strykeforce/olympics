{
  description = "Olympics documentation build shell";
  inputs = {
    nixpkgs.url = github:NixOS/nixpkgs/nixos-unstable;
  };

  outputs = { self, nixpkgs }:
    let
      pkgs = nixpkgs.legacyPackages.x86_64-linux;
    in
    {

      devShell.x86_64-linux = pkgs.mkShell {
        buildInputs = with pkgs; [ nodejs python39Packages.grip ];
      };
    };
}
