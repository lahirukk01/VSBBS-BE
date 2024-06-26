export AWS_DEFAULT_REGION=us-east-1

BASE_REPOSITORY_URI=124984100580.dkr.ecr.us-east-1.amazonaws.com/lkksoftdev/vsbba
SERVERS_LIST=(
"account-service" "api-gateway" "beneficiary-service" "config-server" "eureka-server"
"external-service" "loan-service" "registration-service")

COMMIT_HASH=$(echo "$CODEBUILD_RESOLVED_SOURCE_VERSION" | cut -c 1-7)
IMAGE_TAG=${COMMIT_HASH:=latest}

DOCKER_PASSWORD="$(aws ecr get-login-password --region "$AWS_DEFAULT_REGION")"

# Delete previous images
for server in "${SERVERS_LIST[@]}"; do
  if [ -d "$server" ]; then
    echo "Deleting previous images for $server"
    REPOSITORY_URI=$BASE_REPOSITORY_URI/$server
    docker rmi -f "$(docker images -q "$REPOSITORY_URI")"
  fi
done

for server in "${SERVERS_LIST[@]}"; do
  if [ -d "$server" ] && ! git diff --quiet HEAD^ HEAD "$server"; then
    echo "Processing $server"
    REPOSITORY_URI=$BASE_REPOSITORY_URI/$server

    cd "$server" || exit

    mvn spring-boot:build-image -DskipTests
    echo "$DOCKER_PASSWORD" | docker login --username AWS --password-stdin "$REPOSITORY_URI"
    echo "Image Repo: $REPOSITORY_URI"
    BUILT_IMAGE="$(docker images -q "lkksoftdev/vsbba/$server")"

    if [[ -n $BUILT_IMAGE ]]; then
      echo "Built image: $BUILT_IMAGE"
      docker tag "$BUILT_IMAGE" "$REPOSITORY_URI":latest
      docker tag "$REPOSITORY_URI":latest "$REPOSITORY_URI":"$IMAGE_TAG"
      docker push "$REPOSITORY_URI":latest
      docker push "$REPOSITORY_URI":"$IMAGE_TAG"
    fi

    cd ..
  fi
done


